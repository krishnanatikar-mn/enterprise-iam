package com.enterprise.iam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRoleRequest {

    @NotNull(message = "User Id is required")
    private Long userId;

    @NotNull(message = "Role Id is required")
    private Long roleId;

}