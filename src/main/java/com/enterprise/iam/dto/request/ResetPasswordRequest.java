package com.enterprise.iam.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    @Email
    private String email;

    @NotBlank
    private String otp;

    @NotBlank
    private String newPassword;

}