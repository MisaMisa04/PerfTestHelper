package ru.koshkin.PerfTestHelper.Security;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.koshkin.PerfTestHelper.Entities.User;
import ru.koshkin.PerfTestHelper.Exceptions.BlockedTokenException;
import ru.koshkin.PerfTestHelper.Kafka.KafkaMessage;
import ru.koshkin.PerfTestHelper.Kafka.KafkaSender;
import ru.koshkin.PerfTestHelper.Kafka.KafkaTopic;
import ru.koshkin.PerfTestHelper.enums.TokenStatus;
import ru.koshkin.PerfTestHelper.services.JwtService;
import ru.koshkin.PerfTestHelper.services.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public KafkaSender kafkaSender;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Получаем токен из заголовка
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Обрезаем префикс и получаем имя пользователя из токена
        var jwt = authHeader.substring(BEARER_PREFIX.length());
        try {
            var userId = jwtService.extractUserId(jwt);// на этом моменте проверяется и валидность токена + время истечения
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = (User) userService.loadUserByUserId(userId);

                // Если токен валиден, то аутентифицируем пользователя
                if (jwtService.isTokenValid(jwt, user) && !tokenIsBlocked(jwt, user)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                    sendAuthEventToKafka(user, request);
                }
            }
        } catch (ExpiredJwtException expired) {
            makeInvalidTokenErrorResponse(response, TokenStatus.EXPIRED);
        } catch (BlockedTokenException blocked) {
            makeInvalidTokenErrorResponse(response, TokenStatus.BLOCKED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private void sendAuthEventToKafka(User user, HttpServletRequest request) {
        KafkaMessage message = KafkaMessage.builder()
                .topic(KafkaTopic.AUTHENTIFICATION_TOPIC)
                .value(String.format("User %s authenticated during the request of %s",
                        user.getUsername(),
                        request.getMethod() + ' ' + request.getRequestURI())
                )
                .build();
        kafkaSender.sendMessage(message);
    }

    private boolean tokenIsBlocked(String jwt, User user) throws BlockedTokenException {
        if (user.getLastBlockedAt() == null) return false;
        if (jwtService.extractIssuedAt(jwt).isBefore(user.getLastBlockedAt()))
            throw new BlockedTokenException();
        else return false;
    }

    private void makeInvalidTokenErrorResponse(HttpServletResponse response, TokenStatus tokenStatus) {
        Gson gson = new Gson();
        Map<String, String> error = new HashMap<>();
        error.put("error_type", "invalid token");
        error.put("error_reason", tokenStatus.name());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(error));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
