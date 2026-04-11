package com.adps.sistemaBancario.service;

import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService {

    private final SecretKey chaveSecreta;
    private final long expiracao;

    public JWTService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiracao
    ) {
        this.chaveSecreta = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiracao = expiracao;
    }

    public String gerarToken(String email){
        Date agora = new Date();
        Date validade = new  Date(agora.getTime() + expiracao);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(agora)
                .setExpiration(validade)
                .signWith(chaveSecreta,  SignatureAlgorithm.HS256)
                .compact();
    }

    public String validarToken(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(chaveSecreta)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
