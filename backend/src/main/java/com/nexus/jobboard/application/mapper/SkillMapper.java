package com.nexus.jobboard.application.mapper;

import com.nexus.jobboard.application.dto.response.SkillResponse;
import com.nexus.jobboard.domain.model.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Skill mapper following SRP
 * - Single responsibility: Map between Skill entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface SkillMapper {
    
    @Mapping(target = "jobCount", expression = "java(skill.getJobCount())")
    SkillResponse toResponse(Skill skill);
}
