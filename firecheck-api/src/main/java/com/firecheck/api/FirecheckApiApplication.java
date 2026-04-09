package com.firecheck.api;

import com.firecheck.api.model.Usuario;
import com.firecheck.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FirecheckApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirecheckApiApplication.class, args);
    }

    @Bean
    CommandLineRunner init(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("--- INICIANDO VERIFICAÇÃO DE CARGA DE DADOS ---");

            // 1. Criar Usuário Admin (ESSENCIAL PARA ACESSAR O SISTEMA)
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNomeCompleto("Administrador do Sistema");
                admin.setLogin("admin");
                admin.setSenha(passwordEncoder.encode("admin"));
                admin.setCpf("000.000.000-00");
                admin.setEmail("admin@firecheck.com");
                admin.setTelefone("(00) 0000-0000");
                admin.setPerfil("ADMIN");
                
                usuarioRepository.save(admin);
                System.out.println(">>> ADMIN CRIADO (Login: admin / Senha: admin)");
            } else {
                System.out.println(">>> Usuários já existem. Pulando criação de Admin.");
            }

            System.out.println(">>> BANCO DE DADOS LIMPO E PRONTO PARA USO! <<<");
        };
    }
}