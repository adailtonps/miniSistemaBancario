package com.adps.sistemaBancario.service;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.ResetSenhaToken;
import com.adps.sistemaBancario.exception.OperacaoInvalidaException;
import com.adps.sistemaBancario.exception.SenhaInvalidaException;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.repository.ResetSenhaTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final ClienteRepository clienteRepository;
    private final ResetSenhaTokenRepository resetSenhaTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${frontend.url}")
    private String frontendUrl;

    public AuthService(ClienteRepository clienteRepository, ResetSenhaTokenRepository resetSenhaTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.resetSenhaTokenRepository = resetSenhaTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public String esqueciSenha(String email) {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isEmpty()) {
            return null;
        }

        Cliente cliente = clienteOpt.get();

        resetSenhaTokenRepository.findByCliente(cliente)
                .ifPresent(resetSenhaTokenRepository::delete);

        String tokenReset = UUID.randomUUID().toString();

        ResetSenhaToken reset = new ResetSenhaToken();
        reset.setToken(tokenReset);
        reset.setCliente(cliente);
        reset.setExpiracao(LocalDateTime.now().plusMinutes(30));
        reset.setUsado(false);

        resetSenhaTokenRepository.save(reset);

        String link = frontendUrl + "/reset-senha.html?token=" + tokenReset;

        emailService.enviar(
                cliente.getEmail(),
                "Redefinição de senha",
                tokenReset
        );
        return link;

    }

    public void resetarSenha(String token, String novaSenha) {

        boolean encontrouNumero = false;
        boolean encontrouLetra = false;
        boolean encontrouEspecial = false;


        ResetSenhaToken resetToken = resetSenhaTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new OperacaoInvalidaException("Token inválido!"));

        if (resetToken.isUsado()) {
            throw new OperacaoInvalidaException("Token já utilizado!");
        }
        if (resetToken.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new OperacaoInvalidaException("Token expirado!");
        }

        validarSenha(novaSenha);

        Cliente cliente = resetToken.getCliente();
        cliente.setSenhaCliente(passwordEncoder.encode(novaSenha));
        clienteRepository.save(cliente);

        resetToken.setUsado(true);
        resetSenhaTokenRepository.save(resetToken);
    }

    private void validarSenha(String senha) {
        if(senha == null || senha.length() < 8) {
            throw new SenhaInvalidaException();
        }
        boolean numero = false;
        boolean maiuscula = false;
        boolean especial = false;

        for(char c : senha.toCharArray()) {
            if(Character.isDigit(c)) numero = true;
            if(Character.isUpperCase(c)) maiuscula = true;
            if("!@#$%*?".contains(String.valueOf(c))) especial = true;
        }

        if(!numero || !maiuscula || !especial) {
            throw new SenhaInvalidaException();
        }

    }
}
