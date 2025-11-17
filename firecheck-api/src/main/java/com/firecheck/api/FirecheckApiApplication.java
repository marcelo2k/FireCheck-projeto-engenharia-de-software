package com.firecheck.api;

import com.firecheck.api.model.Usuario;
import com.firecheck.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FirecheckApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirecheckApiApplication.class, args);
    }

    // Este método roda toda vez que o servidor inicia
    @Bean
    CommandLineRunner init(UsuarioRepository repository) {
        return args -> {
            // Verifica se existe algum usuário no banco
            if (repository.count() == 0) {
                // Se não existir, cria o Admin padrão
                Usuario admin = new Usuario();
                admin.setNomeCompleto("Administrador do Sistema");
                admin.setLogin("admin");
                admin.setSenha("admin"); // Senha simples para teste
                admin.setCpf("000.000.000-00");
                admin.setEmail("admin@firecheck.com");
                admin.setTelefone("(00) 0000-0000");
                
                repository.save(admin);
                
                System.out.println("------------------------------------------------");
                System.out.println(" USUÁRIO ADMIN CRIADO AUTOMATICAMENTE");
                System.out.println(" Login: admin");
                System.out.println(" Senha: admin");
                System.out.println("------------------------------------------------");
            }
        };
    }
}