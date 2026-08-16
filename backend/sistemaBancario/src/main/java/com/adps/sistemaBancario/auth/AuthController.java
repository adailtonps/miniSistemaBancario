package com.adps.sistemaBancario.auth;

import com.adps.sistemaBancario.domain.Cliente;
import com.adps.sistemaBancario.domain.Conta;
import com.adps.sistemaBancario.dto.*;
import com.adps.sistemaBancario.repository.ClienteRepository;
import com.adps.sistemaBancario.service.AuthService;
import com.adps.sistemaBancario.service.CadastroService;
import com.adps.sistemaBancario.service.JWTService;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${frontend.url}")
    private String frontendUrl;

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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getSenha()
                )
        );

        System.out.println("AUTENTICAÇÃO: " + authentication);
        System.out.println("NOME: " + authentication.getName());

        String token = jwtService.gerarToken(authentication);

        System.out.println("TOKEN GERADO: " + token);

        LoginResponseDTO response = new LoginResponseDTO(token);

        System.out.println("TOKEN NO DTO: " + response.getToken());

        return ResponseEntity.ok(response);
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
