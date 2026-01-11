package com.membership.users.application.service;

import com.membership.users.application.dto.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private static final long EXPIRATION = 3600;

    public LoginResponseDTO generateToken(Long userId, String email, List<String> roles) {

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .claim("userId", userId)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRATION))
                .build();

        return new LoginResponseDTO(jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(), EXPIRATION);
    }

    public LoginResponseDTO generateExpiredToken(Long userId, String email, List<String> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .claim("userId", userId)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(now.minusSeconds(7200))
                .expiresAt(now.minusSeconds(3600))
                .build();

        return new LoginResponseDTO(jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(), -3600);
    }
}
