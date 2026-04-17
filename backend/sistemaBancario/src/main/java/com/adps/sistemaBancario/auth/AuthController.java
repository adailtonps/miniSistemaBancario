package com.adps.sistemaBancario.auth;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.dto.*;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.service.AuthService;
import com.adps.sistemaBancario.service.CadastroService;
import com.adps.sistemaBancario.service.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private JWTService  jwtService;
    private AuthenticationManager authenticationManager;
    private CadastroService cadastroService;
    private ClienteRepository  clienteRepository;
    private AuthService authService;

    public AuthController(JWTService jwtService, AuthenticationManager authenticationManager, CadastroService cadastroService, ClienteRepository clienteRepository, AuthService authService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.cadastroService = cadastroService;
        this.clienteRepository = clienteRepository;
        this.authService = authService;
    }


    @PostMapping("/cadastro")
    public ResponseEntity<ClienteResponseDTO> cadastrar(
            @RequestBody CriarContaDTO criarContaDTO) {

        Conta conta = cadastroService.cadastrarCliente(criarContaDTO);

        Cliente cliente = conta.getCliente();

        return ResponseEntity.ok(
                new ClienteResponseDTO(
                        cliente.getId(),
                        cliente.getNome(),
                        cliente.getEmail()
                )
        );
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login (@RequestBody LoginDTO dto, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getSenha())
        );
        String token = jwtService.gerarToken(dto.getEmail());
        ResponseCookie responseCookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(60 * 60)
                .path("/")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.ok(new LoginResponseDTO("Login realizado com sucesso!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDTO> logout(HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha (
            @RequestBody EsqueciSenhaDTO esqueciSenhaDTO) {
        String link = authService.esqueciSenha(esqueciSenhaDTO.getEmail());

        if (link == null) {
            return ResponseEntity.ok("Se o email existir, você receberá instruções para redefinir a senha.");
        }
        return ResponseEntity.ok(link);
    }

    @PostMapping("/reset-senha")
    public ResponseEntity<String> resetSenha (
            @RequestBody ResetSenhaDTO resetSenhaDTO)
    { authService.resetarSenha(resetSenhaDTO.getToken(), resetSenhaDTO.getNovaSenha());
        return ResponseEntity.ok("Senha alterada com sucesso!");
    }
}
