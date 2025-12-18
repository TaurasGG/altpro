package com.taurasg.altpro.api.repository;

import com.taurasg.altpro.api.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByOrganizationId(String organizationId);
}
