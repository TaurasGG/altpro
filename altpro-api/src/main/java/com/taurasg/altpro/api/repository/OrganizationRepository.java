package com.taurasg.altpro.api.repository;

import com.taurasg.altpro.api.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganizationRepository extends MongoRepository<Organization, String> { }

