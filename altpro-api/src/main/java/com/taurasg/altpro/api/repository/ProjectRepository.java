package com.taurasg.altpro.api.repository;

import com.taurasg.altpro.api.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> { }