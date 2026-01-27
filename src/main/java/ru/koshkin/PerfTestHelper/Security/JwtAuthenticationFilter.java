package ru.koshkin.PerfTestHelper.Security;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.koshkin.PerfTestHelper.enums.TokenStatus;
import ru.koshkin.PerfTestHelper.services.JwtService;
import ru.koshkin.PerfTestHelper.services.UserService;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

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
            var username = jwtService.extractUserName(jwt);// на этом моменте проверяется и валидность токена + время истечения
            final TokenStatus tokenStatusFromDB = jwtService.getTokenStatusFromDB(username, jwt);
            if (tokenStatusFromDB == TokenStatus.BLOCKED) {
//                токен заблокирован - отправляем об этом ответ и не аутентифицируем
                makeInvalidTokenErrorResponse(response, TokenStatus.BLOCKED);
            } else if (tokenStatusFromDB == TokenStatus.ACTIVE && StringUtils.isNotEmpty(username) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);

                // Если токен валиден, то аутентифицируем пользователя
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            }
        } catch (ExpiredJwtException expired) {
//            токен истёк - ставим ему истекание в базе, добавляем инфу в ответ
            jwtService.markJwtExpired(jwt);
            makeInvalidTokenErrorResponse(response, TokenStatus.EXPIRED);
        } finally {
            filterChain.doFilter(request, response);
        }
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
