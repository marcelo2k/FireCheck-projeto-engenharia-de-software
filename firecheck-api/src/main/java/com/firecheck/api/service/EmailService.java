package com.firecheck.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Função que gera um código aleatório de 6 dígitos
    public String gerarCodigoVerificacao() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000); // Garante que será entre 100000 e 999999
        return String.valueOf(codigo);
    }

    // Função que monta e envia o e-mail
    public void enviarEmailRecuperacao(String emailDestino, String codigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        
        mensagem.setTo(emailDestino);
        mensagem.setSubject("FireCheck - Código de Recuperação de Senha");
        mensagem.setText("Olá!\n\n"
                + "Você solicitou a redefinição de senha no sistema FireCheck.\n"
                + "Seu código de segurança é: " + codigo + "\n\n"
                + "Insira este código na tela de login para criar sua nova senha.\n"
                + "Se você não solicitou essa alteração, por favor, ignore este e-mail.");
        
        // Dispara o e-mail através da conta do Google que você configurou
        mailSender.send(mensagem);
    }
}