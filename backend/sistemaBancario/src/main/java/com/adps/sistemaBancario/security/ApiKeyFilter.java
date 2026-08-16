package com.adps.sistemaBancario.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    @Value("${BANCO_API_KEY}")
    private String bancoApiKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (path.equals("/pagamento/gerar")) {
            String apiKey = request.getHeader("X-Service-Key");
            if (apiKey == null || !apiKey.equals(bancoApiKey)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            filterChain.doFilter(request, response);
        }
    }
}
