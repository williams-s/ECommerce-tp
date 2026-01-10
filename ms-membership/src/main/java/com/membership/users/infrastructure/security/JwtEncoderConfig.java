package com.membership.users.infrastructure.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtEncoderConfig {

    @Bean
    JwtEncoder jwtEncoder() throws Exception {

        InputStream publicKeyStream =
                new ClassPathResource("public_key.pem").getInputStream();
        InputStream privateKeyStream =
                new ClassPathResource("private_key.pem").getInputStream();

        RSAPublicKey publicKey =
                RsaKeyConverters.x509().convert(publicKeyStream);
        RSAPrivateKey privateKey =
                RsaKeyConverters.pkcs8().convert(privateKeyStream);

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        JWKSource<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }
}
