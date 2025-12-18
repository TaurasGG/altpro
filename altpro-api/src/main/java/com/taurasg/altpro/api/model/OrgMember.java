package com.taurasg.altpro.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgMember {
    private String userId;
    private OrgRole role;
}
