package com.adps.sistemaBancario.security;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        throws ServletException, IOException{

        SecurityContextHolder.clearContext();
        System.out.println("JWT FILTER EXECUTOU");


        String header = request.getHeader("Authorization");

        System.out.println("HEADER: " + header);
        if(header != null && header.startsWith("Bearer ")){
            String token = header.replace("Bearer ", "");

            System.out.println("TOKEN ENCONTRADO");
            String email = jwtService.validarToken(token);

            System.out.println("EMAIL DO TOKEN: " + email);
            if (email != null){
                Cliente cliente = clienteRepository.findByEmail(email).orElse(null);

                System.out.println("CLIENTE ENCONTRADO: " + cliente);

                if(cliente != null){
                    UsernamePasswordAuthenticationToken auth=
                            new UsernamePasswordAuthenticationToken(
                                    cliente,
                                    null,
                                    List.of()
                            );
                          SecurityContextHolder.getContext().setAuthentication(auth);

                    System.out.println("AUTH CRIADA: " + auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
