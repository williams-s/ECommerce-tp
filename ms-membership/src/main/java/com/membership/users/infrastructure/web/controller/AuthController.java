package com.membership.users.infrastructure.web.controller;

import com.membership.users.application.dto.LoginRequestDTO;
import com.membership.users.application.dto.LoginResponseDTO;
import com.membership.users.application.service.AuthService;
import com.membership.users.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Login de l'utilisateur",
               description = "Authentifie l'utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur authentifié avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                     schema = @Schema())),
        @ApiResponse(responseCode = "404", description = "Utilisateur non rencontré",
                    content = @Content)
    })
    @RequestMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(LoginRequestDTO loginRequestDTO) {
        String email = loginRequestDTO.getEmail();
        List<String> roles = List.of("USER");
        Long userId = userService.getUserByEmail(email).getId();
        log.info("POST /api/v1/auth/login - Authentification de l'utilisateur");
        LoginResponseDTO loginResponse = authService.generateToken(userId, email, roles);
        return ResponseEntity.ok(loginResponse);
    }
}
