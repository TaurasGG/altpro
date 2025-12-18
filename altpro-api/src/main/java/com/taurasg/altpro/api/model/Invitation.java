package com.taurasg.altpro.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    @Id
    private String id;
    private String organizationId;
    private String inviterUserId;
    private String inviteeUsername;
    private String inviteeEmail;
    private String status; // PENDING, ACCEPTED, DECLINED
    private Instant createdAt;
}

