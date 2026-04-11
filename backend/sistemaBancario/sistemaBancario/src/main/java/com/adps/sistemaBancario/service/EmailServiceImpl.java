package com.adps.sistemaBancario.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void enviar(String para, String assunto, String token) {
        System.out.println("===== EMAIL ======");
        System.out.println("Para: "+para);
        System.out.println("Assunto: "+assunto);

        String resetLink = frontendUrl+"/reset-senha.html?token="+token;


        System.out.println(resetLink);
        System.out.println("===================");
    }
}
