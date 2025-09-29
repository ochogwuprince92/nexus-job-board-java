package com.nexus.jobboard.application.mapper;

import com.nexus.jobboard.application.dto.response.CompanyResponse;
import com.nexus.jobboard.domain.model.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Company mapper following SRP
 * - Single responsibility: Map between Company entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper {
    
    @Mapping(target = "activeJobCount", expression = "java(company.getActiveJobCount())")
    CompanyResponse toResponse(Company company);
}
