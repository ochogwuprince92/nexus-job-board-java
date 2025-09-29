package com.nexus.jobboard.application.mapper;

import com.nexus.jobboard.application.dto.response.UserResponse;
import com.nexus.jobboard.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * User mapper following SRP
 * - Single responsibility: Map between User entity and DTOs
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    UserResponse toResponse(User user);
}
