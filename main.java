package main;

import java.util.Scanner;
import java.util.List;
import model.Usuario;
import model.Cliente;
import service.UsuarioService;
import service.ClienteService;

public class main {

    private static UsuarioService usuarioService = new UsuarioService();
    private static ClienteService clienteService = new ClienteService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("== Bem-vindo ao Backend do Firecheck [Básico] ==");
        
        while (true) {
            exibirMenu();
            int opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    listarUsuarios();
                    break;
                case 3:
                    cadastrarCliente();
                    break;
                case 4:
                    listarClientes();
                    break;
                case 0:
                    System.out.println("Saindo do sistema...");
                    return; 
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Cadastrar Usuário (RF01)");
        System.out.println("2. Listar Usuários");
        System.out.println("3. Cadastrar Cliente (RF04)");
        System.out.println("4. Listar Clientes");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void cadastrarUsuario() {
        System.out.println("\n-- Cadastro de Usuário --");
        System.out.print("Nome Completo: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String tel = scanner.nextLine();
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario novoUsuario = new Usuario(nome, cpf, email, tel, login, senha);
        
        String resultado = usuarioService.cadastrarUsuario(novoUsuario);
        System.out.println(resultado);
    }

    private static void listarUsuarios() {
        System.out.println("\n-- Lista de Usuários Cadastrados --");
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
        } else {
            for (Usuario u : usuarios) {
                System.out.println(u.toString());
            }
        }
    }

    private static void cadastrarCliente() {
        System.out.println("\n-- Cadastro de Cliente --");
        System.out.print("Razão Social: ");
        String razao = scanner.nextLine();
        System.out.print("CNPJ/CPF: ");
        String cnpjCpf = scanner.nextLine();
        System.out.print("Endereço: ");
        String end = scanner.nextLine();
        System.out.print("Telefone: ");
        String tel = scanner.nextLine();
        System.out.print("Responsável: ");
        String resp = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        
        Cliente novoCliente = new Cliente(razao, cnpjCpf, end, tel, resp, email);
        String resultado = clienteService.cadastrarCliente(novoCliente);
        System.out.println(resultado);
    }

    private static void listarClientes() {
        System.out.println("\n-- Lista de Clientes Cadastrados --");
        List<Cliente> clientes = clienteService.listarClientes();
        
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            for (Cliente c : clientes) {
                System.out.println(c.toString());
            }
        }
    }
}