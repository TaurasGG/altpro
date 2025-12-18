package com.taurasg.altpro.api.repository;

import com.taurasg.altpro.api.model.Invitation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvitationRepository extends MongoRepository<Invitation, String> {
    List<Invitation> findByInviteeEmail(String email);
    List<Invitation> findByInviteeUsername(String username);
    List<Invitation> findByOrganizationId(String organizationId);
}

