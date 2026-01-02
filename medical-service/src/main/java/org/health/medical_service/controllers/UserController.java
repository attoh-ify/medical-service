package org.health.medical_service.controllers;

import org.health.medical_service.dto.LoginResponseDto;
import org.health.medical_service.dto.ResponseDto;
import org.health.medical_service.dto.UserDto;
import org.health.medical_service.mappers.UserMapper;
import org.health.medical_service.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseDto registerUser(@RequestBody UserDto dto) {
        return new ResponseDto(
                "User registered",
                userMapper.toDto(
                        userService.registerUser(
                                userMapper.fromDto(dto)
                        )
                )
        );
    }

    @PostMapping("/login")
    public ResponseDto loginUser(@RequestBody UserDto dto) {
        String token = userService.loginUser(userMapper.fromDto(dto));
        return new ResponseDto(
                "User logged in",
                new LoginResponseDto(token)
        );
    }

    @GetMapping("/{email}")
    public ResponseDto getDetails(@PathVariable String email) {
        return new ResponseDto(
                "User fetched",
                userMapper.toDto(
                        userService.getUserDetails(email)
                )
        );
    }
}
