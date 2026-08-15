package com.enterprise.iam.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "login_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String ipAddress;

    @Column(nullable = false)
    private boolean successful;

    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime loginTime;

    @PrePersist
    public void prePersist() {
        if (loginTime == null) {
            loginTime = LocalDateTime.now();
        }
    }
}