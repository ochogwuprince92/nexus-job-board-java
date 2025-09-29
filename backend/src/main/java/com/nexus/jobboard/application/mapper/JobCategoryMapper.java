package com.nexus.jobboard.application.mapper;

import com.nexus.jobboard.application.dto.response.JobCategoryResponse;
import com.nexus.jobboard.domain.model.JobCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Job category mapper following SRP
 * - Single responsibility: Map between JobCategory entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface JobCategoryMapper {
    
    @Mapping(target = "jobCount", expression = "java(jobCategory.getJobCount())")
    @Mapping(target = "activeJobCount", expression = "java(jobCategory.getActiveJobCount())")
    JobCategoryResponse toResponse(JobCategory jobCategory);
}
