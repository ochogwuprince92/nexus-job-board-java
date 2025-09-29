package com.nexus.jobboard.application.mapper;

import com.nexus.jobboard.application.dto.response.JobApplicationResponse;
import com.nexus.jobboard.domain.model.JobApplication;
import org.mapstruct.Mapper;

/**
 * Job application mapper following SRP
 * - Single responsibility: Map between JobApplication entity and DTOs
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, JobMapper.class})
public interface JobApplicationMapper {
    
    JobApplicationResponse toResponse(JobApplication jobApplication);
}
