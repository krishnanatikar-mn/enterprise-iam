package com.enterprise.iam.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private Long id;

    private String roleName;

    private String description;

    private Set<String> permissions;

}