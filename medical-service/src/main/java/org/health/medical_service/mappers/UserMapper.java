package org.health.medical_service.mappers;

import org.health.medical_service.dto.UserDto;
import org.health.medical_service.entities.User;

public interface UserMapper {
    User fromDto(UserDto userDto);
    UserDto toDto(User user);
}
