package org.health.medical_service.mappers.impl;

import org.health.medical_service.dto.UserDto;
import org.health.medical_service.entities.User;
import org.health.medical_service.mappers.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public User fromDto(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.email(),
                userDto.password(),
                userDto.role()
        );
    }

    @Override
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}
