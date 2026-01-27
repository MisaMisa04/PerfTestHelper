package ru.koshkin.PerfTestHelper.Entities;

import jakarta.persistence.*;
import lombok.*;
import ru.koshkin.PerfTestHelper.enums.TokenStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "jwt_tokens", indexes = {
        @Index(name = "idx_jwt_tokens_token", columnList = "token"),
        @Index(name = "idx_jwt_tokens_user_id", columnList = "user_id"),
        @Index(name = "idx_jwt_tokens_status", columnList = "status"),
        @Index(name = "idx_jwt_tokens_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class JwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 512, nullable = false)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private TokenStatus status;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;
}
