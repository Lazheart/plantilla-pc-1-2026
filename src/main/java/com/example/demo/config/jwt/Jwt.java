package com.example.demo.config.jwt;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class Jwt {

    @Value("${spring.app.jwt.secret}")
    private String secret;

    @Value("${spring.app.jwt.expiration}")
    private long expiration;

    /**
     * Retorna la clave secreta decodificada para la firma de JWT.
     * Soporta secretos en Base64 o en texto plano.
     */
    public SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Retorna la duración de expiración en milisegundos.
     * Si 'expiration' en la propiedad es menor a 1,000,000 se asume que está en segundos.
     */
    public long getExpirationInMillis() {
        return expiration < 1000000L ? expiration * 1000L : expiration;
    }
}
