package com.nexus.jobboard.application.mapper;

import com.nexus.jobboard.application.dto.response.JobResponse;
import com.nexus.jobboard.domain.model.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Job mapper following SRP
 * - Single responsibility: Map between Job entity and DTOs
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, CompanyMapper.class, JobCategoryMapper.class, SkillMapper.class})
public interface JobMapper {
    
    @Mapping(target = "applicationCount", expression = "java(job.getApplicationCount())")
    JobResponse toResponse(Job job);
}
