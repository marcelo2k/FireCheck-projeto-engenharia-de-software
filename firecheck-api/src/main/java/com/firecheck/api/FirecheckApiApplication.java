package com.firecheck.api;

import com.firecheck.api.model.Cliente;
import com.firecheck.api.model.Edificacao;
import com.firecheck.api.model.Equipamento;
import com.firecheck.api.model.Usuario;
import com.firecheck.api.repository.ClienteRepository;
import com.firecheck.api.repository.EdificacaoRepository;
import com.firecheck.api.repository.EquipamentoRepository;
import com.firecheck.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class FirecheckApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirecheckApiApplication.class, args);
    }

    @Bean
    CommandLineRunner init(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            EdificacaoRepository edificacaoRepository,
            EquipamentoRepository equipamentoRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("--- INICIANDO VERIFICAÇÃO DE CARGA DE DADOS ---");

            // 1. Criar Usuário Admin (se não existir nenhum usuário)
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

            // 2. Carregar Dados do Estudo de Caso (se não existirem clientes)
            if (clienteRepository.count() == 0) {
                System.out.println(">>> Banco de clientes vazio. Iniciando carga de dados de exemplo...");

                // --- A. Clientes ---
                Cliente c1 = new Cliente("Supermercado Central Ltda", "11.222.333/0001-44", "Centro Comercial", "(14) 3322-1111", "Gerente", "contato@central.com");
                c1 = clienteRepository.save(c1); // Salva e recupera com ID
                
                Cliente c2 = new Cliente("Condomínio Edifício Seguro", "55.666.777/0001-88", "Bairro Alto", "(14) 3324-5555", "Síndico", "sindico@edseguro.com");
                c2 = clienteRepository.save(c2);

                // --- B. Edificações ---
                // Vincula ao c1 (Supermercado) que já tem ID
                Edificacao e1 = new Edificacao("Loja Centro", "Rua das Flores, 123", "19900-001", c1);
                e1 = edificacaoRepository.save(e1);

                Edificacao e2 = new Edificacao("Depósito", "Av. Industrial, 500", "19901-120", c1);
                e2 = edificacaoRepository.save(e2);
                
                // Vincula ao c2 (Condomínio)
                Edificacao e3 = new Edificacao("Bloco A", "Rua da Paz, 987", "19905-200", c2);
                e3 = edificacaoRepository.save(e3);

                // --- C. Equipamentos ---
                // Vincula à e1 (Loja)
                Equipamento eq1 = new Equipamento(); 
                eq1.setTipoEquipamento("Extintor PQS 4kg"); 
                eq1.setLocalizacao("Frente de Caixa 03"); 
                eq1.setDataFabricacao(LocalDate.parse("2025-01-10")); 
                eq1.setDataValidade(LocalDate.parse("2026-01-10"));
                eq1.setEdificacao(e1);
                equipamentoRepository.save(eq1);

                Equipamento eq2 = new Equipamento(); 
                eq2.setTipoEquipamento("Hidrante"); 
                eq2.setLocalizacao("Estacionamento"); 
                eq2.setDataFabricacao(LocalDate.parse("2020-05-15")); 
                eq2.setDataValidade(LocalDate.parse("2030-05-15"));
                eq2.setEdificacao(e1);
                equipamentoRepository.save(eq2);

                // Vincula à e2 (Depósito)
                Equipamento eq3 = new Equipamento(); 
                eq3.setTipoEquipamento("Detector de Fumaça"); 
                eq3.setLocalizacao("Corredor do Depósito"); 
                eq3.setDataFabricacao(LocalDate.parse("2022-11-20")); 
                eq3.setDataValidade(LocalDate.parse("2027-11-20"));
                eq3.setEdificacao(e2);
                equipamentoRepository.save(eq3);

                System.out.println(">>> DADOS DE EXEMPLO CARREGADOS COM SUCESSO! <<<");
            } else {
                System.out.println(">>> Clientes já existem no banco. Pulando carga de dados de exemplo.");
            }
        };
    }
}