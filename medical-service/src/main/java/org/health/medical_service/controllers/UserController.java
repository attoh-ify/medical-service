package org.health.medical_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.health.medical_service.dto.LoginDto;
import org.health.medical_service.dto.LoginResponseDto;
import org.health.medical_service.dto.ResponseDto;
import org.health.medical_service.dto.UserDto;
import org.health.medical_service.mappers.UserMapper;
import org.health.medical_service.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Users",
        description = "User registration, authentication, and profile management"
)
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided registration details"
    )
    public ResponseDto registerUser(
            @RequestBody UserDto dto
    ) {
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
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using email and password and returns an access token"
    )
    public ResponseDto loginUser(
            @RequestBody LoginDto dto
    ) {
        String token = userService.loginUser(dto);
        return new ResponseDto(
                "User logged in",
                new LoginResponseDto(token)
        );
    }

    @GetMapping("/{email}")
    @Operation(
            summary = "Get user profile",
            description = "Retrieves user profile information using the user's email address"
    )
    public ResponseDto getDetails(
            @Parameter(
                    description = "User email address",
                    example = "jane.doe@email.com",
                    required = true
            )
            @PathVariable String email
    ) {
        return new ResponseDto(
                "User fetched",
                userMapper.toDto(
                        userService.getUserDetails(email)
                )
        );
    }
}
