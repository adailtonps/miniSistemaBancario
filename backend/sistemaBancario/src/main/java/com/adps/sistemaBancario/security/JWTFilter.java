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

        throws ServletException, IOException{

        SecurityContextHolder.clearContext();

        String token = null;

        if(request.getCookies() != null){
           for(var cookie : request.getCookies()){
               if(cookie.getName().equals("token")){
                   token = cookie.getValue();
               }
           }
        }

        if(token != null){
            try{

            var claims = jwtService.getClaims(token);
            String email = claims.getSubject();

            if(email != null) {
                Cliente cliente = clienteRepository
                        .findByEmail(email)
                        .orElse(null);

                if (cliente != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            cliente,
                            null,
                            List.of()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch(Exception ex){
                System.out.println("Token inválido!");}
        }
    filterChain.doFilter(request, response);}
}
