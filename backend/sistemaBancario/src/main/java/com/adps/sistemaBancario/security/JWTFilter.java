package com.adps.sistemaBancario.security;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)

        throws ServletException, IOException {


        SecurityContextHolder.clearContext();
        System.out.println("JWT FILTER EXECUTOU");

        String authHeader = request.getHeader("Authorization");
        String token = null;

        System.out.println("HEADER: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            if (token != null) {
                try {
                    var claims = jwtService.getClaim(token);
                    String email = claims.getSubject();
                    String role = claims.get("role").toString();

                    if (email != null && role != null) {
                        Cliente cliente = clienteRepository.findByEmail(email).orElse(null);

                        if (cliente != null) {
                            var authorities =List.of(new SimpleGrantedAuthority("ROLE_" + role));

                            var authentication = new UsernamePasswordAuthenticationToken(
                                    cliente,
                                    null,
                                    authorities
                            );
                            SecurityContextHolder.
                                    getContext()
                                    .setAuthentication(authentication);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } filterChain.doFilter(request, response);
    }
}
