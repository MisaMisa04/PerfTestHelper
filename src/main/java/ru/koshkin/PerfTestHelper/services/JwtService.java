package ru.koshkin.PerfTestHelper.services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.koshkin.PerfTestHelper.Entities.JwtToken;
import ru.koshkin.PerfTestHelper.Entities.User;
import ru.koshkin.PerfTestHelper.enums.TokenStatus;
import ru.koshkin.PerfTestHelper.repositories.JwtTokenRepo;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // TODO сгенерить норм ключ: https://habr.com/ru/articles/784508/

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Value("${token.expiration.seconds}")
    private String tokenExpirationSeconds;

    @Autowired
    private JwtTokenRepo tokenRepo;

    /**
     * Проверка, что присланный токен принадлежит пользователю и он активен
     *
     * @param username
     * @param token
     * @return true/false
     */
    public TokenStatus getTokenStatusFromDB(String username, String token) {
        return tokenRepo.getTokenStatusByUserNameAndToken(username, token);
    }

    public void markJwtExpired(String expiredJWT) {
        // в отдельном треде - отпускаем основной, чтобы вернуть пользователю инфу
        Thread t = new Thread(() -> {
            JwtToken token = tokenRepo.getByToken(expiredJWT);
            token.setStatus(TokenStatus.EXPIRED);
            tokenRepo.save(token);
        });
        t.start();
    }

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User userEntity) {
            claims.put("id", userEntity.getId());
            claims.put("username", userEntity.getUsername());
        }
        assert userDetails instanceof User;
        return generateToken(claims, (User) userDetails);
    }

    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерация токена
     *
     * @param extraClaims дополнительные данные
     * @param user        Entity пользователя
     * @return токен
     */
    private String generateToken(Map<String, Object> extraClaims, User user) {
        String token = Jwts.builder().setClaims(extraClaims).setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * Long.parseLong(tokenExpirationSeconds)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        // сохранение токена в БД
        // TODO поля ipAddress, userAgent, пока NULL
        JwtToken tokenEntity = JwtToken.builder()
                .token(token)
                .userId(user.getId())
                .status(TokenStatus.ACTIVE)
                .issuedAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(Long.parseLong(tokenExpirationSeconds)))
                .build();
        tokenRepo.save(tokenEntity);
        return token;
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    /**
     * Получение ключа для подписи токена
     *
     * @return ключ
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

