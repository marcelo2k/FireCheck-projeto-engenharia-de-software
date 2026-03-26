package com.firecheck.api.service;

import com.firecheck.api.model.Cliente;
import com.firecheck.api.repository.ClienteRepository;
// Importar EdificacaoRepository para verificar dependências antes de deletar (opcional, mas bom)
import com.firecheck.api.repository.EdificacaoRepository;
import jakarta.persistence.EntityNotFoundException; // Para erro 404
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired // Injetar para verificar se há edificações ligadas
    private EdificacaoRepository edificacaoRepository;

    public String cadastrarCliente(Cliente cliente) {
        Optional<Cliente> existente = clienteRepository.findByCnpjCpf(cliente.getCnpjCpf());
        if (existente.isPresent()) {
            return "Erro: CNPJ/CPF já cadastrado para outro cliente.";
        }
        clienteRepository.save(cliente);
        return "Cliente cadastrado com sucesso!";
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente buscarClientePorId(Long id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        // Lança exceção se não encontrar, para Controller retornar 404
        return clienteOpt.orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + id + " não encontrado."));
    }

    // --- NOVO MÉTODO: ATUALIZAR ---
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        // 1. Busca o cliente existente
        Cliente clienteExistente = buscarClientePorId(id); // Reusa o método que já lança 404 se não achar

        // 2. Valida se o CNPJ/CPF atualizado já não pertence a OUTRO cliente
        Optional<Cliente> conflitoCnpj = clienteRepository.findByCnpjCpf(clienteAtualizado.getCnpjCpf());
        // Se encontrou alguém com esse CNPJ E o ID é DIFERENTE do que estamos atualizando
        if (conflitoCnpj.isPresent() && !conflitoCnpj.get().getId().equals(id)) {
            throw new IllegalArgumentException("Erro: CNPJ/CPF já pertence a outro cliente cadastrado.");
        }

        // 3. Atualiza os dados do cliente existente com os novos dados
        // (Não atualizamos o ID)
        clienteExistente.setRazaoSocial(clienteAtualizado.getRazaoSocial());
        clienteExistente.setCnpjCpf(clienteAtualizado.getCnpjCpf());
        clienteExistente.setEndereco(clienteAtualizado.getEndereco());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setResponsavel(clienteAtualizado.getResponsavel());
        clienteExistente.setEmail(clienteAtualizado.getEmail());

        // 4. Salva (o save do JPA entende que é um update por causa do ID existente)
        return clienteRepository.save(clienteExistente);
    }
    // --- FIM NOVO MÉTODO ---

    // --- NOVO MÉTODO: DELETAR ---
    public void deletarCliente(Long id) {
        // 1. Verifica se o cliente existe (lança 404 se não)
        if (!clienteRepository.existsById(id)) {
             throw new EntityNotFoundException("Cliente com ID " + id + " não encontrado.");
        }

        // 2. (Opcional, mas recomendado) Verifica se existem Edificações associadas
        // Precisamos garantir que EdificacaoRepository já foi migrado para JPA para usar findByClienteId
        // Se EdificacaoRepository ainda usa lista estática, essa verificação não funcionará 100%
        // Assumindo que EdificacaoRepository JÁ É JPA:
        if (!edificacaoRepository.findByClienteId(id).isEmpty()) {
            throw new IllegalStateException("Erro: Não é possível excluir o cliente pois ele possui edificações associadas.");
        }
        // Se EdificacaoRepository AINDA NÃO É JPA, comente a linha acima.

        // 3. Deleta o cliente
        clienteRepository.deleteById(id);
    }
    // --- FIM NOVO MÉTODO ---
}