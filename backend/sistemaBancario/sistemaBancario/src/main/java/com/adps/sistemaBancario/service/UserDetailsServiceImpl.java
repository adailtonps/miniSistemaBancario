package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.exception.NegocioException;
import com.adps.sistemaBancario.repository.ClienteRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ClienteRepository clienteRepository;

    public UserDetailsServiceImpl(ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Cliente cliente = clienteRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        return org.springframework.security.core.userdetails.User
                .withUsername(cliente.getEmail())
                .password(cliente.getSenhaCliente())
                .authorities("USER")
                .build();
    }
}
