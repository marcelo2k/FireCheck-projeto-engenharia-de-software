// URLs das APIs
const API_URL_USUARIOS = 'http://localhost:8080/api/usuarios';
const API_URL_CLIENTES = 'http://localhost:8080/api/clientes';
const API_URL_EDIFICACOES = 'http://localhost:8080/api/edificacoes';
const API_URL_EQUIPAMENTOS = 'http://localhost:8080/api/equipamentos';
const API_URL_INSPECOES = 'http://localhost:8080/api/inspecoes';
const API_URL_ITENS = 'http://localhost:8080/api/itens-inspecionados';
const API_URL_NAOCONFORMIDADES = 'http://localhost:8080/api/nao-conformidades';
const API_URL_SERVICOS = 'http://localhost:8080/api/servicos';
const API_URL_ORCAMENTOS = 'http://localhost:8080/api/orcamentos';
const API_URL_ORDENS_SERVICO = 'http://localhost:8080/api/ordens-servico';

// --- LÓGICA DE AUTENTICAÇÃO (LOGIN) ---
// Verifica se existe um usuário salvo no navegador
const usuarioLogado = localStorage.getItem('usuarioLogado');

// Se NÃO tiver usuário e não estivermos na página de login, expulsa para o login
if (!usuarioLogado) {
    window.location.href = 'login.html';
} else {
    // Se tiver logado, podemos mostrar o nome no console ou na tela
    const user = JSON.parse(usuarioLogado);
    console.log(`Usuário logado: ${user.nomeCompleto}`);
}

// Configura o botão SAIR
const btnLogout = document.getElementById('btnLogout');
if (btnLogout) {
    btnLogout.addEventListener('click', () => {
        localStorage.removeItem('usuarioLogado'); // Apaga o "crachá"
        window.location.href = 'login.html';     // Manda para fora
    });
}
// ---------------------------------------

// Variáveis para controle de edição
let idClienteEditando = null;
let idUsuarioEditando = null;
let idEdificacaoEditando = null;
let idEquipamentoEditando = null;
let idInspecaoEditando = null;
let itemInspecEditando = null;
let idNCEditando = null;
let idServicoEditando = null;
let itensOrcamentoRequest = [];
let servicosCatalogo = [];

// --- Helper para tratar erros de fetch ---
async function handleFetchResponse(response, expectJson = true) {
    if (!response.ok) {
        const errorText = await response.text();
        console.error("Erro da API:", response.url, response.status, errorText);
        throw new Error(`HTTP error! status: ${response.status} - ${errorText || response.statusText}`);
    }
    const text = await response.text();
    if (!text && expectJson) return [];
    if (!text && !expectJson) return "";
    if (expectJson) {
        try {
             if (text.trim() === '[]') return [];
            return JSON.parse(text);
        } catch (e) {
            console.error("Falha ao parsear JSON:", text);
            throw new Error("Resposta inválida do servidor (não é JSON).");
        }
    }
    return text;
}


// --- USUÁRIOS ---
const formUsuario = document.getElementById('formUsuario');
const btnListarUsuarios = document.getElementById('btnListarUsuarios');
const listaUsuarios = document.getElementById('listaUsuarios');

formUsuario.addEventListener('submit', async (e) => {
    e.preventDefault();
    const usuario = {
        nomeCompleto: document.getElementById('usuarioNome').value, cpf: document.getElementById('usuarioCpf').value,
        email: document.getElementById('usuarioEmail').value, telefone: document.getElementById('usuarioTelefone').value,
        login: document.getElementById('usuarioLogin').value, senha: document.getElementById('usuarioSenha').value
    };
    if (idUsuarioEditando && !usuario.senha) { delete usuario.senha; }
    const url = idUsuarioEditando ? `${API_URL_USUARIOS}/${idUsuarioEditando}` : API_URL_USUARIOS;
    const method = idUsuarioEditando ? 'PUT' : 'POST';
    try {
        const response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(usuario) });
        const resultadoTexto = await response.text();
        if (response.ok) {
            let successMsg = resultadoTexto;
            if (method === 'PUT') { try { const userAtualizado = JSON.parse(resultadoTexto); successMsg = `Usuário ID ${userAtualizado.id} atualizado!`; } catch(e) { successMsg = `Usuário ID ${idUsuarioEditando} atualizado!`;} }
            alert(successMsg);
            cancelarEdicaoUsuario(); listarUsuarios(); popularDropdownTecnicos(); popularDropdownTecnicosParaOrcamento();
        } else { alert(`Erro ao ${method === 'PUT' ? 'atualizar' : 'cadastrar'} usuário: ${resultadoTexto}`); }
    } catch (error) { alert(`Erro ao conectar: ` + error.message); console.error("Fetch Error:", error); }
});

async function listarUsuarios() {
    try {
        const response = await fetch(API_URL_USUARIOS); const usuarios = await handleFetchResponse(response);
        listaUsuarios.innerHTML = '<h3>Usuários:</h3>';
        if (!Array.isArray(usuarios)) { listaUsuarios.innerHTML = '<p style="color:red;">Erro: Resposta inválida.</p>'; return; }
        if (usuarios.length === 0) { listaUsuarios.innerHTML += '<p>Nenhum.</p>'; }
        else { usuarios.forEach(u => { const texto = `ID=${u.id}, Nome=${u.nomeCompleto}, Login=${u.login}`; listaUsuarios.innerHTML += `<div>${texto} <button class="btn-editar-usuario" data-id="${u.id}">Editar</button> <button class="btn-excluir-usuario" data-id="${u.id}">Excluir</button></div>`; }); adicionarListenersBotoesUsuario(); }
    } catch (error) { console.error('Erro ao listar usuários: ' + error.message); listaUsuarios.innerHTML = '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarUsuarios.addEventListener('click', listarUsuarios);

async function carregarUsuarioParaEdicao(id) {
    try {
        const response = await fetch(`${API_URL_USUARIOS}/${id}`); if (!response.ok) throw new Error(`Usuário não encontrado (ID: ${id})`); const usuario = await handleFetchResponse(response);
        document.getElementById('usuarioNome').value = usuario.nomeCompleto || ''; document.getElementById('usuarioCpf').value = usuario.cpf || '';
        document.getElementById('usuarioEmail').value = usuario.email || ''; document.getElementById('usuarioTelefone').value = usuario.telefone || '';
        document.getElementById('usuarioLogin').value = usuario.login || ''; document.getElementById('usuarioSenha').value = ''; document.getElementById('usuarioSenha').placeholder = 'Deixe em branco para não alterar';
        idUsuarioEditando = id; formUsuario.querySelector('button[type="submit"]').textContent = 'Salvar Alterações';
    } catch (error) { alert('Erro ao carregar usuário: ' + error.message); console.error(error); cancelarEdicaoUsuario(); }
}
function cancelarEdicaoUsuario() {
    idUsuarioEditando = null; formUsuario.reset(); document.getElementById('usuarioSenha').placeholder = 'Senha'; formUsuario.querySelector('button[type="submit"]').textContent = 'Cadastrar Usuário';
}
async function deletarUsuario(id) {
    if (!confirm(`Excluir usuário ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_USUARIOS}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Usuário excluído!'); listarUsuarios(); popularDropdownTecnicos(); popularDropdownTecnicosParaOrcamento(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesUsuario() {
     document.querySelectorAll('.btn-editar-usuario').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-usuario').forEach(b => b.replaceWith(b.cloneNode(true)));
     document.querySelectorAll('.btn-editar-usuario').forEach(b => b.addEventListener('click', (e) => carregarUsuarioParaEdicao(e.target.getAttribute('data-id'))));
     document.querySelectorAll('.btn-excluir-usuario').forEach(b => b.addEventListener('click', (e) => deletarUsuario(e.target.getAttribute('data-id'))));
}

// --- CLIENTES ---
const formCliente = document.getElementById('formCliente');
const btnListarClientes = document.getElementById('btnListarClientes');
const listaClientes = document.getElementById('listaClientes');

formCliente.addEventListener('submit', async (e) => {
    e.preventDefault();
    const cliente = {
        razaoSocial: document.getElementById('clienteRazao').value, cnpjCpf: document.getElementById('clienteCnpjCpf').value,
        endereco: document.getElementById('clienteEndereco').value, telefone: document.getElementById('clienteTelefone').value,
        responsavel: document.getElementById('clienteResponsavel').value, email: document.getElementById('clienteEmail').value
    };
    const url = idClienteEditando ? `${API_URL_CLIENTES}/${idClienteEditando}` : API_URL_CLIENTES;
    const method = idClienteEditando ? 'PUT' : 'POST';
    try {
        const response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(cliente) });
        if (response.ok) {
            const resultado = method === 'PUT' ? await handleFetchResponse(response) : await response.text();
            alert(method === 'PUT' ? `Cliente ID ${resultado.id} atualizado!` : resultado);
            cancelarEdicaoCliente(); listarClientes(); popularDropdownClientes();
        } else { const errorMsg = await response.text(); alert(`Erro ao ${method === 'PUT' ? 'atualizar' : 'cadastrar'} cliente: ${errorMsg}`); }
    } catch (error) { alert(`Erro ao conectar: ` + error.message); console.error(error); }
});

async function listarClientes() {
    try {
        const response = await fetch(API_URL_CLIENTES); const clientes = await handleFetchResponse(response);
        listaClientes.innerHTML = '<h3>Clientes:</h3>';
        if (!Array.isArray(clientes)) throw new Error("Resposta inválida (não é array)");
        if (clientes.length === 0) { listaClientes.innerHTML += '<p>Nenhum.</p>'; }
        else { clientes.forEach(c => { const texto = `ID=${c.id}, Razão=${c.razaoSocial}, CNPJ/CPF=${c.cnpjCpf}`; listaClientes.innerHTML += `<div>${texto} <button class="btn-editar-cliente" data-id="${c.id}">Editar</button> <button class="btn-excluir-cliente" data-id="${c.id}">Excluir</button></div>`; }); adicionarListenersBotoesCliente(); }
    } catch (error) { alert('Erro ao listar clientes: ' + error.message); console.error(error); listaClientes.innerHTML = '<p style="color:red;">Erro ao carregar.</p>'; }
}
btnListarClientes.addEventListener('click', listarClientes);

async function carregarClienteParaEdicao(id) {
    try {
        const response = await fetch(`${API_URL_CLIENTES}/${id}`); if (!response.ok) throw new Error(`Cliente não encontrado (ID: ${id})`); const cliente = await handleFetchResponse(response);
        document.getElementById('clienteRazao').value = cliente.razaoSocial || ''; document.getElementById('clienteCnpjCpf').value = cliente.cnpjCpf || '';
        document.getElementById('clienteEndereco').value = cliente.endereco || ''; document.getElementById('clienteTelefone').value = cliente.telefone || '';
        document.getElementById('clienteResponsavel').value = cliente.responsavel || ''; document.getElementById('clienteEmail').value = cliente.email || '';
        idClienteEditando = id; formCliente.querySelector('button[type="submit"]').textContent = 'Salvar Alterações';
    } catch (error) { alert('Erro ao carregar cliente: ' + error.message); console.error(error); cancelarEdicaoCliente(); }
}
function cancelarEdicaoCliente() {
    idClienteEditando = null; formCliente.reset(); formCliente.querySelector('button[type="submit"]').textContent = 'Cadastrar Cliente';
}
async function deletarCliente(id) {
    if (!confirm(`Excluir cliente ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_CLIENTES}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Cliente excluído!'); listarClientes(); popularDropdownClientes(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesCliente() {
     document.querySelectorAll('.btn-editar-cliente').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-cliente').forEach(b => b.replaceWith(b.cloneNode(true)));
     document.querySelectorAll('.btn-editar-cliente').forEach(b => b.addEventListener('click', (e) => carregarClienteParaEdicao(e.target.getAttribute('data-id'))));
     document.querySelectorAll('.btn-excluir-cliente').forEach(b => b.addEventListener('click', (e) => deletarCliente(e.target.getAttribute('data-id'))));
}

// --- EDIFICAÇÕES ---
const formEdificacao = document.getElementById('formEdificacao');
const selectClienteEdificacao = document.getElementById('edificacaoClienteId');
const btnListarEdificacoes = document.getElementById('btnListarEdificacoes');
const listaEdificacoes = document.getElementById('listaEdificacoes');

async function popularDropdownClientes() {
     try {
        const response = await fetch(API_URL_CLIENTES); const clientes = await handleFetchResponse(response);
        if (!selectClienteEdificacao) { console.warn("Dropdown 'edificacaoClienteId' não encontrado."); return; }
        selectClienteEdificacao.innerHTML = '<option value="">-- Selecione um Cliente --</option>';
        if (!Array.isArray(clientes)) { console.error("Clientes não é array!"); return; }
        if (clientes.length > 0) { clientes.forEach(c => { selectClienteEdificacao.innerHTML += `<option value="${c.id}">${c.razaoSocial} (ID: ${c.id})</option>`; }); }
    } catch (error) { console.error('Erro ao popular clientes:', error); if (selectClienteEdificacao) selectClienteEdificacao.innerHTML = '<option value="">Erro!</option>';}
}

formEdificacao.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        nome: document.getElementById('edificacaoNome').value, endereco: document.getElementById('edificacaoEndereco').value,
        cep: document.getElementById('edificacaoCep').value, idCliente: document.getElementById('edificacaoClienteId').value
    };
    if (!payload.idCliente) { alert("Selecione um cliente."); return; }
    const url = idEdificacaoEditando ? `${API_URL_EDIFICACOES}/${idEdificacaoEditando}` : API_URL_EDIFICACOES;
    const method = idEdificacaoEditando ? 'PUT' : 'POST';
    try {
        const response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        if (response.ok) {
            const dto = await handleFetchResponse(response);
            alert(`Edificação ID ${dto.idEdificacao} ${method === 'PUT' ? 'atualizada' : 'cadastrada'}!`);
            cancelarEdicaoEdificacao(); listarEdificacoes(); popularDropdownEdificacoesParaEquipamento(); popularDropdownEdificacoesParaInspecao(); popularDropdownEdificacoesParaOrcamento();
        } else { const errorMsg = await response.text(); alert(`Erro ao ${method === 'PUT' ? 'atualizar' : 'cadastrar'} edificação: ${errorMsg}`); }
    } catch (error) { alert(`Erro ao conectar: ` + error.message); console.error(error); }
});

async function listarEdificacoes() {
    try {
        const response = await fetch(API_URL_EDIFICACOES); const edificacoes = await handleFetchResponse(response);
        listaEdificacoes.innerHTML = '<h3>Edificações:</h3>';
        if (!Array.isArray(edificacoes)) throw new Error("Resposta inválida (não é array)");
        if (edificacoes.length === 0) { listaEdificacoes.innerHTML += '<p>Nenhuma.</p>'; }
        else { edificacoes.forEach(e => { const texto = `ID=${e.idEdificacao}, Nome=${e.nome}, ClienteID=${e.idCliente}`; listaEdificacoes.innerHTML += `<div>${texto} <button class="btn-editar-edificacao" data-id="${e.idEdificacao}">Editar</button> <button class="btn-excluir-edificacao" data-id="${e.idEdificacao}">Excluir</button></div>`; }); adicionarListenersBotoesEdificacao(); }
    } catch (error) { alert('Erro ao listar edificações: ' + error.message); console.error(error); listaEdificacoes.innerHTML = '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarEdificacoes.addEventListener('click', listarEdificacoes);

async function carregarEdificacaoParaEdicao(id) {
    try {
        const response = await fetch(`${API_URL_EDIFICACOES}/${id}`); if (!response.ok) throw new Error(`Edificação não encontrada (ID: ${id})`); const edificacao = await handleFetchResponse(response);
        document.getElementById('edificacaoNome').value = edificacao.nome || ''; document.getElementById('edificacaoEndereco').value = edificacao.endereco || '';
        document.getElementById('edificacaoCep').value = edificacao.cep || ''; selectClienteEdificacao.value = edificacao.idCliente;
        idEdificacaoEditando = id; formEdificacao.querySelector('button[type="submit"]').textContent = 'Salvar Alterações';
    } catch (error) { alert('Erro ao carregar edificação: ' + error.message); console.error(error); cancelarEdicaoEdificacao(); }
}
function cancelarEdicaoEdificacao() {
    idEdificacaoEditando = null; formEdificacao.reset(); formEdificacao.querySelector('button[type="submit"]').textContent = 'Cadastrar Edificação';
}
async function deletarEdificacao(id) {
    if (!confirm(`Excluir edificação ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_EDIFICACOES}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Edificação excluída!'); listarEdificacoes(); popularDropdownEdificacoesParaEquipamento(); popularDropdownEdificacoesParaInspecao(); popularDropdownEdificacoesParaOrcamento(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesEdificacao() {
    document.querySelectorAll('.btn-editar-edificacao').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-edificacao').forEach(b => b.replaceWith(b.cloneNode(true)));
    document.querySelectorAll('.btn-editar-edificacao').forEach(b => b.addEventListener('click', (e) => carregarEdificacaoParaEdicao(e.target.getAttribute('data-id'))));
    document.querySelectorAll('.btn-excluir-edificacao').forEach(b => b.addEventListener('click', (e) => deletarEdificacao(e.target.getAttribute('data-id'))));
}


// --- EQUIPAMENTOS ---
const formEquipamento = document.getElementById('formEquipamento');
const selectEdificacaoEquip = document.getElementById('equipamentoEdificacaoId');
const btnListarEquipamentos = document.getElementById('btnListarEquipamentos');
const listaEquipamentos = document.getElementById('listaEquipamentos');

async function popularDropdownEdificacoesParaEquipamento() {
     try {
        const response = await fetch(API_URL_EDIFICACOES); const edificacoes = await handleFetchResponse(response);
        if (!selectEdificacaoEquip) { console.warn("Dropdown 'equipamentoEdificacaoId' não encontrado."); return; }
        selectEdificacaoEquip.innerHTML = '<option value="">-- Edificação --</option>';
        if (Array.isArray(edificacoes) && edificacoes.length > 0) {
            edificacoes.forEach(e => { selectEdificacaoEquip.innerHTML += `<option value="${e.idEdificacao}">${e.nome} (ID: ${e.idEdificacao})</option>`; });
        }
    } catch (error) { console.error('Erro ao popular edificações (equipamento):', error.message); if(selectEdificacaoEquip) selectEdificacaoEquip.innerHTML = '<option value="">Erro!</option>';}
}

formEquipamento.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        tipoEquipamento: document.getElementById('equipamentoTipo').value, localizacao: document.getElementById('equipamentoLocal').value,
        dataFabricacao: document.getElementById('equipamentoFab').value, dataValidade: document.getElementById('equipamentoVal').value,
        idEdificacao: document.getElementById('equipamentoEdificacaoId').value
    };
    if (!payload.idEdificacao || !payload.dataFabricacao || !payload.dataValidade || !payload.tipoEquipamento || !payload.localizacao) { alert("Preencha todos os campos."); return; }
    const url = idEquipamentoEditando ? `${API_URL_EQUIPAMENTOS}/${idEquipamentoEditando}` : API_URL_EQUIPAMENTOS;
    const method = idEquipamentoEditando ? 'PUT' : 'POST';
    try {
        const response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        const dto = await handleFetchResponse(response);
        alert(`Equipamento ID ${dto.idEquipamento} ${method === 'PUT' ? 'atualizado' : 'cadastrado'}!`);
        cancelarEdicaoEquipamento(); listarEquipamentos(); popularDropdownEquipamentos();
    } catch (error) { alert(`Erro ao conectar: ` + error.message); console.error(error); }
});

async function listarEquipamentos() {
    try {
        const response = await fetch(API_URL_EQUIPAMENTOS); const equipamentos = await handleFetchResponse(response);
        listaEquipamentos.innerHTML = '<h3>Equipamentos:</h3>';
        if (!Array.isArray(equipamentos)) throw new Error("Resposta inválida");
        if (equipamentos.length === 0) { listaEquipamentos.innerHTML += '<p>Nenhum.</p>'; }
        else { equipamentos.forEach(e => { const texto = `ID=${e.idEquipamento}, Tipo=${e.tipoEquipamento}, EdifID=${e.idEdificacao}`; listaEquipamentos.innerHTML += `<div>${texto} <button class="btn-editar-equipamento" data-id="${e.idEquipamento}">Editar</button> <button class="btn-excluir-equipamento" data-id="${e.idEquipamento}">Excluir</button></div>`; }); adicionarListenersBotoesEquipamento(); }
    } catch (error) { alert('Erro ao listar equipamentos: ' + error.message); console.error(error); listaEquipamentos.innerHTML = '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarEquipamentos.addEventListener('click', listarEquipamentos);

async function carregarEquipamentoParaEdicao(id) {
    try {
        const response = await fetch(`${API_URL_EQUIPAMENTOS}/${id}`); const equipamento = await handleFetchResponse(response);
        document.getElementById('equipamentoTipo').value = equipamento.tipoEquipamento || ''; document.getElementById('equipamentoLocal').value = equipamento.localizacao || '';
        document.getElementById('equipamentoFab').value = equipamento.dataFabricacao || ''; document.getElementById('equipamentoVal').value = equipamento.dataValidade || '';
        selectEdificacaoEquip.value = equipamento.idEdificacao; idEquipamentoEditando = id;
        formEquipamento.querySelector('button[type="submit"]').textContent = 'Salvar Alterações';
    } catch (error) { alert('Erro ao carregar equipamento: ' + error.message); console.error(error); cancelarEdicaoEquipamento(); }
}
function cancelarEdicaoEquipamento() {
    idEquipamentoEditando = null; formEquipamento.reset(); formEquipamento.querySelector('button[type="submit"]').textContent = 'Cadastrar Equipamento';
}
async function deletarEquipamento(id) {
    if (!confirm(`Excluir equipamento ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_EQUIPAMENTOS}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Equipamento excluído!'); listarEquipamentos(); popularDropdownEquipamentos(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesEquipamento() {
    document.querySelectorAll('.btn-editar-equipamento').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-equipamento').forEach(b => b.replaceWith(b.cloneNode(true)));
    document.querySelectorAll('.btn-editar-equipamento').forEach(b => b.addEventListener('click', (e) => carregarEquipamentoParaEdicao(e.target.getAttribute('data-id'))));
    document.querySelectorAll('.btn-excluir-equipamento').forEach(b => b.addEventListener('click', (e) => deletarEquipamento(e.target.getAttribute('data-id'))));
}


// --- INSPEÇÕES ---
const formInspecao = document.getElementById('formInspecao');
const selectInspecaoEdificacao = document.getElementById('inspecaoEdificacaoId');
const selectInspecaoTecnico = document.getElementById('inspecaoTecnicoId');
const btnListarInspecoes = document.getElementById('btnListarInspecoes');
const listaInspecoes = document.getElementById('listaInspecoes');

async function popularDropdownEdificacoesParaInspecao() {
     try {
        const response = await fetch(API_URL_EDIFICACOES); const edificacoes = await handleFetchResponse(response);
        if (!selectInspecaoEdificacao) { console.warn("Dropdown 'inspecaoEdificacaoId' não encontrado."); return; }
        selectInspecaoEdificacao.innerHTML = '<option value="">-- Edificação --</option>';
        if (Array.isArray(edificacoes) && edificacoes.length > 0) {
            edificacoes.forEach(e => { selectInspecaoEdificacao.innerHTML += `<option value="${e.idEdificacao}">${e.nome} (ID: ${e.idEdificacao})</option>`; });
        }
    } catch (error) { console.error('Erro ao popular edificações (inspeção):', error.message); if(selectInspecaoEdificacao) selectInspecaoEdificacao.innerHTML = '<option value="">Erro!</option>';}
 }
async function popularDropdownTecnicos() {
      try {
        const response = await fetch(API_URL_USUARIOS); const usuarios = await handleFetchResponse(response);
        if(!selectInspecaoTecnico) { console.warn("Dropdown 'inspecaoTecnicoId' não encontrado."); }
        else {
            selectInspecaoTecnico.innerHTML = '<option value="">-- Técnico --</option>';
            if (Array.isArray(usuarios) && usuarios.length > 0) {
                usuarios.forEach(u => { selectInspecaoTecnico.innerHTML += `<option value="${u.id}">${u.nomeCompleto} (ID: ${u.id})</option>`; });
            }
        }
    } catch (error) { console.error('Erro ao popular técnicos (inspeção):', error.message);
        if(selectInspecaoTecnico) selectInspecaoTecnico.innerHTML = '<option value="">Erro!</option>';
    }
 }

formInspecao.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        dataInspecao: document.getElementById('inspecaoData').value, status: document.getElementById('inspecaoStatus').value,
        idEdificacao: document.getElementById('inspecaoEdificacaoId').value, idTecnico: document.getElementById('inspecaoTecnicoId').value
    };
    if (!payload.idEdificacao || !payload.idTecnico || !payload.dataInspecao || !payload.status) { alert("Preencha todos os campos."); return; }
    const url = idInspecaoEditando ? `${API_URL_INSPECOES}/${idInspecaoEditando}` : API_URL_INSPECOES;
    const method = idInspecaoEditando ? 'PUT' : 'POST';
    try {
        const response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
         if (response.ok) { const dto = await handleFetchResponse(response); alert(`Inspeção ID ${dto.idInspecao} ${method === 'PUT' ? 'atualizada' : 'agendada'}!`); }
         else { const errorMsg = await response.text(); alert("Erro: " + errorMsg); }
        cancelarEdicaoInspecao(); listarInspecoes(); popularDropdownInspecoes();
    } catch (error) { alert(`Erro ao conectar: ` + error.message); console.error(error); }
});

async function listarInspecoes() {
    try {
        const response = await fetch(API_URL_INSPECOES); const inspecoes = await handleFetchResponse(response);
        listaInspecoes.innerHTML = '<h3>Inspeções:</h3>';
        if (!Array.isArray(inspecoes)) throw new Error("Resposta inválida");
        if (inspecoes.length === 0) { listaInspecoes.innerHTML += '<p>Nenhuma.</p>'; }
        else { inspecoes.forEach(i => { const texto = `ID=${i.idInspecao}, Status=${i.status}, Edif=${i.nomeEdificacao}, Tec=${i.nomeTecnico}`; listaInspecoes.innerHTML += `<div>${texto} <button class="btn-editar-inspecao" data-id="${i.idInspecao}">Editar</button> <button class="btn-excluir-inspecao" data-id="${i.idInspecao}">Excluir</button></div>`; }); adicionarListenersBotoesInspecao(); }
    } catch (error) { alert('Erro ao listar inspeções: ' + error.message); console.error(error); listaInspecoes.innerHTML += '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarInspecoes.addEventListener('click', listarInspecoes);

async function carregarInspecaoParaEdicao(id) {
    try {
        const response = await fetch(`${API_URL_INSPECOES}/${id}`); const inspecao = await handleFetchResponse(response);
        const dataFormatada = inspecao.dataInspecao ? inspecao.dataInspecao.substring(0, 16) : '';
        document.getElementById('inspecaoData').value = dataFormatada;
        document.getElementById('inspecaoStatus').value = inspecao.status || '';
        selectInspecaoEdificacao.value = inspecao.idEdificacao; selectInspecaoTecnico.value = inspecao.idTecnico;
        idInspecaoEditando = id; formInspecao.querySelector('button[type="submit"]').textContent = 'Salvar Alterações';
    } catch (error) { alert('Erro ao carregar inspeção: ' + error.message); console.error(error); cancelarEdicaoInspecao(); }
}
function cancelarEdicaoInspecao() {
    idInspecaoEditando = null; formInspecao.reset(); formInspecao.querySelector('button[type="submit"]').textContent = 'Agendar Inspeção';
}
async function deletarInspecao(id) {
    if (!confirm(`Excluir inspeção ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_INSPECOES}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Inspeção excluída!'); listarInspecoes(); popularDropdownInspecoes(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesInspecao() {
    document.querySelectorAll('.btn-editar-inspecao').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-inspecao').forEach(b => b.replaceWith(b.cloneNode(true)));
    document.querySelectorAll('.btn-editar-inspecao').forEach(b => b.addEventListener('click', (e) => carregarInspecaoParaEdicao(e.target.getAttribute('data-id'))));
    document.querySelectorAll('.btn-excluir-inspecao').forEach(b => b.addEventListener('click', (e) => deletarInspecao(e.target.getAttribute('data-id'))));
}


// --- ITENS INSPECIONADOS ---
const formItemInspecionado = document.getElementById('formItemInspecionado');
const selectItemInspecao = document.getElementById('itemInspecaoId');
const selectItemEquipamento = document.getElementById('itemEquipamentoId');
const btnListarItens = document.getElementById('btnListarItens');
const listaItensInspecionados = document.getElementById('listaItensInspecionados');
const filtroInspecao = document.getElementById('filtroInspecaoId');
//let itemInspecEditando = null;

async function popularDropdownInspecoes() {
    try {
        const response = await fetch(API_URL_INSPECOES); const inspecoes = await handleFetchResponse(response);
        const selects = [selectItemInspecao, filtroInspecao, selectNCInspecao, filtroNCInspecao];
        selects.forEach((select, index) => {
            if (select) {
                let defaultText = "-- Selecione Inspeção --";
                if (select.id === 'filtroInspecaoId' || select.id === 'filtroNCInspecaoId') { defaultText = "-- Filtrar por Inspeção --"; }
                select.innerHTML = `<option value="">${defaultText}</option>`;
                if (Array.isArray(inspecoes) && inspecoes.length > 0) {
                    inspecoes.forEach(i => { const opt = `<option value="${i.idInspecao}">ID: ${i.idInspecao} (${i.nomeEdificacao} - ${i.status})</option>`; select.innerHTML += opt; });
                }
            } else { const ids = ['itemInspecaoId', 'filtroInspecaoId', 'ncInspecaoId', 'filtroNCInspecaoId']; console.warn(`Dropdown '${ids[index]}' não encontrado.`); }
        });
    } catch (error) { console.error('Erro ao popular dropdowns de inspeções:', error.message);
         const selects = [selectItemInspecao, filtroInspecao, selectNCInspecao, filtroNCInspecao];
         selects.forEach(select => { if(select) select.innerHTML = '<option value="">Erro!</option>'; });
    }
}
async function popularDropdownEquipamentos() {
     try {
        const response = await fetch(API_URL_EQUIPAMENTOS); const equipamentos = await handleFetchResponse(response);
        if (selectItemEquipamento) {
            selectItemEquipamento.innerHTML = '<option value="">-- Equipamento --</option>';
            if (Array.isArray(equipamentos) && equipamentos.length > 0) {
                equipamentos.forEach(e => { selectItemEquipamento.innerHTML += `<option value="${e.idEquipamento}">${e.tipoEquipamento} (ID: ${e.idEquipamento})</option>`; });
            }
        } else { console.warn("Dropdown 'itemEquipamentoId' não encontrado."); }
        if(selectNCEquipamento) {
             selectNCEquipamento.innerHTML = '<option value="">-- Equipamento --</option>';
            if (Array.isArray(equipamentos) && equipamentos.length > 0) {
                equipamentos.forEach(e => { selectNCEquipamento.innerHTML += `<option value="${e.idEquipamento}">${e.tipoEquipamento} (ID: ${e.idEquipamento})</option>`; });
            }
        } else { console.warn("Dropdown 'ncEquipamentoId' não encontrado."); }
    } catch (error) { console.error('Erro ao popular equipamentos:', error.message); if(selectItemEquipamento) selectItemEquipamento.innerHTML = '<option value="">Erro!</option>'; if(selectNCEquipamento) selectNCEquipamento.innerHTML = '<option value="">Erro!</option>';}
}
formItemInspecionado.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        idInspecao: document.getElementById('itemInspecaoId').value, idEquipamento: document.getElementById('itemEquipamentoId').value,
        statusGeral: document.getElementById('itemStatusGeral').value, observacoes: document.getElementById('itemObservacoes').value
    };
    if (!payload.idInspecao || !payload.idEquipamento) { alert("Selecione Inspeção e Equipamento."); return; }
    let url = API_URL_ITENS; let method = 'POST';
    if (itemInspecEditando) {
        url = `${API_URL_ITENS}/inspecao/${itemInspecEditando.idInspecao}/equipamento/${itemInspecEditando.idEquipamento}`;
        method = 'PUT';
        delete payload.idInspecao; delete payload.idEquipamento;
    }
    try {
        const response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        if (response.ok) { const dto = await handleFetchResponse(response); alert(`Item ${method === 'PUT' ? 'atualizado' : 'registrado'} para Inspeção ${dto.idInspecao}.`); }
        else { const errorMsg = await response.text(); alert("Erro: " + errorMsg); }
        cancelarEdicaoItem(); listarItensPorInspecao();
    } catch (error) { alert(`Erro ao conectar: ` + error.message); console.error(error); }
});
async function listarItensPorInspecao() {
    const idInspecao = filtroInspecao.value; if (!idInspecao) { listaItensInspecionados.innerHTML = '<p>Selecione inspeção.</p>'; return; }
    try {
        const response = await fetch(`${API_URL_ITENS}/inspecao/${idInspecao}`); const itens = await handleFetchResponse(response);
        listaItensInspecionados.innerHTML = `<h3>Itens Insp. ${idInspecao}:</h3>`;
        if (!Array.isArray(itens)) throw new Error("Resposta inválida");
        if (itens.length === 0) { listaItensInspecionados.innerHTML += '<p>Nenhum.</p>'; }
        else { itens.forEach(i => { const texto = `Equip ID=${i.idEquipamento} (${i.tipoEquipamento}), Status=${i.statusGeral}`; listaItensInspecionados.innerHTML += `<div>${texto} <button class="btn-editar-item" data-id-inspecao="${i.idInspecao}" data-id-equipamento="${i.idEquipamento}">Editar</button> <button class="btn-excluir-item" data-id-inspecao="${i.idInspecao}" data-id-equipamento="${i.idEquipamento}">Excluir</button></div>`; }); adicionarListenersBotoesItem(); }
    } catch (error) { alert('Erro ao listar itens: ' + error.message); console.error(error); listaItensInspecionados.innerHTML += '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarItens.addEventListener('click', listarItensPorInspecao);
async function carregarItemParaEdicao(idInspecao, idEquipamento) {
    try {
        const response = await fetch(`${API_URL_ITENS}/inspecao/${idInspecao}/equipamento/${idEquipamento}`); const item = await handleFetchResponse(response);
        selectItemInspecao.value = item.idInspecao; selectItemEquipamento.value = item.idEquipamento;
        document.getElementById('itemStatusGeral').value = item.statusGeral || ''; document.getElementById('itemObservacoes').value = item.observacoes || '';
        selectItemInspecao.disabled = true; selectItemEquipamento.disabled = true;
        itemInspecEditando = { idInspecao, idEquipamento };
        formItemInspecionado.querySelector('button[type="submit"]').textContent = 'Salvar Alterações';
    } catch (error) { alert('Erro ao carregar item: ' + error.message); console.error(error); cancelarEdicaoItem(); }
}
function cancelarEdicaoItem() {
    itemInspecEditando = null; formItemInspecionado.reset();
    selectItemInspecao.disabled = false; selectItemEquipamento.disabled = false;
    formItemInspecionado.querySelector('button[type="submit"]').textContent = 'Registrar Item';
}
async function deletarItem(idInspecao, idEquipamento) {
    if (!confirm(`Excluir item (Insp ${idInspecao}, Equip ${idEquipamento})?`)) return;
    try {
        const response = await fetch(`${API_URL_ITENS}/inspecao/${idInspecao}/equipamento/${idEquipamento}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Item excluído!'); listarItensPorInspecao(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesItem() {
    document.querySelectorAll('.btn-editar-item').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-item').forEach(b => b.replaceWith(b.cloneNode(true)));
    document.querySelectorAll('.btn-editar-item').forEach(b => b.addEventListener('click', (e) => carregarItemParaEdicao(e.target.getAttribute('data-id-inspecao'), e.target.getAttribute('data-id-equipamento'))));
    document.querySelectorAll('.btn-excluir-item').forEach(b => b.addEventListener('click', (e) => deletarItem(e.target.getAttribute('data-id-inspecao'), e.target.getAttribute('data-id-equipamento'))));
}


// --- NÃO CONFORMIDADES ---
const formNaoConformidade = document.getElementById('formNaoConformidade');
const selectNCInspecao = document.getElementById('ncInspecaoId');
const selectNCEquipamento = document.getElementById('ncEquipamentoId');
const btnListarNC = document.getElementById('btnListarNC');
const listaNaoConformidades = document.getElementById('listaNaoConformidades');
const filtroNCInspecao = document.getElementById('filtroNCInspecaoId');
//let idNCEditando = null;

formNaoConformidade.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        idInspecao: document.getElementById('ncInspecaoId').value, idEquipamento: document.getElementById('ncEquipamentoId').value,
        descricao: document.getElementById('ncDescricao').value, fotoUrl: document.getElementById('ncFotoUrl').value
    };
     if (!payload.idInspecao || !payload.idEquipamento || !payload.descricao) { alert("Selecione Inspeção, Equipamento e descreva."); return; }
     let url = API_URL_NAOCONFORMIDADES; let method = 'POST';
     if (idNCEditando) {
         url = `${API_URL_NAOCONFORMIDADES}/${idNCEditando}`; method = 'PUT';
         delete payload.idInspecao; delete payload.idEquipamento;
     }
    try {
        const response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        if (response.ok) { const dto = await handleFetchResponse(response); alert(`NC ${method === 'PUT' ? 'atualizada' : 'registrada'} (ID: ${dto.idNaoConformidade}).`); }
        else { const errorMsg = await response.text(); alert("Erro: " + errorMsg); }
        cancelarEdicaoNC(); listarNaoConformidadesPorInspecao();
    } catch (error) { alert(`Erro ao conectar: ` + error.message); console.error(error); }
});
async function listarNaoConformidadesPorInspecao() {
    const idInspecao = filtroNCInspecao.value; if (!idInspecao) { listaNaoConformidades.innerHTML = '<p>Selecione inspeção.</p>'; return; }
    try {
        const response = await fetch(`${API_URL_NAOCONFORMIDADES}/inspecao/${idInspecao}`); const ncs = await handleFetchResponse(response);
        listaNaoConformidades.innerHTML = `<h3>Problemas Insp. ${idInspecao}:</h3>`;
         if (!Array.isArray(ncs)) throw new Error("Resposta inválida");
        if (ncs.length === 0) { listaNaoConformidades.innerHTML += '<p>Nenhum.</p>'; }
        else { ncs.forEach(nc => { const texto = `ID=${nc.idNaoConformidade}, Equip=${nc.idEquipamento} (${nc.tipoEquipamento}), Desc=${nc.descricao}`; listaNaoConformidades.innerHTML += `<div>${texto} <button class="btn-editar-nc" data-id="${nc.idNaoConformidade}">Editar</button> <button class="btn-excluir-nc" data-id="${nc.idNaoConformidade}">Excluir</button></div>`; }); adicionarListenersBotoesNC(); }
    } catch (error) { alert('Erro ao listar NCs: ' + error.message); console.error(error); listaNaoConformidades.innerHTML += '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarNC.addEventListener('click', listarNaoConformidadesPorInspecao);
async function carregarNCParaEdicao(id) {
    try {
        const response = await fetch(`${API_URL_NAOCONFORMIDADES}/${id}`); const nc = await handleFetchResponse(response);
        selectNCInspecao.value = nc.idInspecao; selectNCEquipamento.value = nc.idEquipamento;
        document.getElementById('ncDescricao').value = nc.descricao || ''; document.getElementById('ncFotoUrl').value = nc.fotoUrl || '';
        selectNCInspecao.disabled = true; selectNCEquipamento.disabled = true;
        idNCEditando = id; formNaoConformidade.querySelector('button[type="submit"]').textContent = 'Salvar Alterações';
    } catch (error) { alert('Erro ao carregar NC: ' + error.message); console.error(error); cancelarEdicaoNC(); }
}
function cancelarEdicaoNC() {
    idNCEditando = null; formNaoConformidade.reset();
    selectNCInspecao.disabled = false; selectNCEquipamento.disabled = false;
    formNaoConformidade.querySelector('button[type="submit"]').textContent = 'Registrar Problema';
}
async function deletarNC(id) {
    if (!confirm(`Excluir Não Conformidade ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_NAOCONFORMIDADES}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Não Conformidade excluída!'); listarNaoConformidadesPorInspecao(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesNC() {
    document.querySelectorAll('.btn-editar-nc').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-nc').forEach(b => b.replaceWith(b.cloneNode(true)));
    document.querySelectorAll('.btn-editar-nc').forEach(b => b.addEventListener('click', (e) => carregarNCParaEdicao(e.target.getAttribute('data-id'))));
    document.querySelectorAll('.btn-excluir-nc').forEach(b => b.addEventListener('click', (e) => deletarNC(e.target.getAttribute('data-id'))));
}


// --- SERVIÇOS ---
const formServico = document.getElementById('formServico');
const btnListarServicos = document.getElementById('btnListarServicos');
const listaServicos = document.getElementById('listaServicos');
//let idServicoEditando = null;

formServico.addEventListener('submit', async (e) => {
    e.preventDefault();
    const servico = {
        nome: document.getElementById('servicoNome').value, descricao: document.getElementById('servicoDescricao').value,
        valorUnitario: document.getElementById('servicoValor').value, tempoExecucaoHoras: document.getElementById('servicoTempo').value || null,
        estoque: document.getElementById('servicoEstoque').value || null
    };
    if (!servico.nome || !servico.valorUnitario) { alert("Nome e Valor são obrigatórios."); return; }
    const url = idServicoEditando ? `${API_URL_SERVICOS}/${idServicoEditando}` : API_URL_SERVICOS;
    const method = idServicoEditando ? 'PUT' : 'POST';
    try {
        const response = await fetch(url, { method: method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(servico) });
        if (response.ok) { const dto = await handleFetchResponse(response); alert(`Serviço ID ${dto.idServico} ${method === 'PUT' ? 'atualizado' : 'cadastrado'}!`); }
        else { const errorMsg = await response.text(); alert("Erro: " + errorMsg); }
        cancelarEdicaoServico(); listarServicos(); popularDropdownServicos();
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
});

async function listarServicos() {
    try {
        const response = await fetch(API_URL_SERVICOS); const servicos = await handleFetchResponse(response);
        listaServicos.innerHTML = '<h3>Catálogo Serviços:</h3>';
        if (!Array.isArray(servicos)) throw new Error("Resposta inválida (não é array)");
        if (servicos.length === 0) { listaServicos.innerHTML += '<p>Nenhum.</p>'; }
        else { servicos.forEach(s => { const texto = `ID=${s.idServico}, Nome=${s.nome}, Valor=R$ ${s.valorUnitario}, Estoque=${s.estoque ?? 'N/A'}`; listaServicos.innerHTML += `<div>${texto} <button class="btn-editar-servico" data-id="${s.idServico}">Editar</button> <button class="btn-excluir-servico" data-id="${s.idServico}">Excluir</button></div>`; }); adicionarListenersBotoesServico(); }
    } catch (error) { alert('Erro ao listar serviços: ' + error.message); console.error(error); listaServicos.innerHTML += '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarServicos.addEventListener('click', listarServicos);

async function carregarServicoParaEdicao(id) {
    try {
        const response = await fetch(`${API_URL_SERVICOS}/${id}`); const servico = await handleFetchResponse(response);
        document.getElementById('servicoNome').value = servico.nome || ''; document.getElementById('servicoDescricao').value = servico.descricao || '';
        document.getElementById('servicoValor').value = servico.valorUnitario || ''; document.getElementById('servicoTempo').value = servico.tempoExecucaoHoras || '';
        document.getElementById('servicoEstoque').value = servico.estoque || '';
        idServicoEditando = id; formServico.querySelector('button[type="submit"]').textContent = 'Salvar Alterações';
    } catch (error) { alert('Erro ao carregar serviço: ' + error.message); console.error(error); cancelarEdicaoServico(); }
}
function cancelarEdicaoServico() {
    idServicoEditando = null; formServico.reset(); formServico.querySelector('button[type="submit"]').textContent = 'Cadastrar Serviço';
}
async function deletarServico(id) {
    if (!confirm(`Excluir serviço ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_SERVICOS}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Serviço excluído!'); listarServicos(); popularDropdownServicos(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesServico() {
    document.querySelectorAll('.btn-editar-servico').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-servico').forEach(b => b.replaceWith(b.cloneNode(true)));
    document.querySelectorAll('.btn-editar-servico').forEach(b => b.addEventListener('click', (e) => carregarServicoParaEdicao(e.target.getAttribute('data-id'))));
    document.querySelectorAll('.btn-excluir-servico').forEach(b => b.addEventListener('click', (e) => deletarServico(e.target.getAttribute('data-id'))));
}


// --- ORÇAMENTOS ---
const formOrcamento = document.getElementById('formOrcamento');
const selectOrcamentoEdificacao = document.getElementById('orcamentoEdificacaoId');
const selectOrcamentoUsuario = document.getElementById('orcamentoUsuarioId');
const selectOrcamentoServico = document.getElementById('orcamentoServicoId');
const inputOrcamentoQtd = document.getElementById('orcamentoItemQtd');
const btnAdicionarItem = document.getElementById('btnAdicionarItemOrcamento');
const listaItensAdicionadosDiv = document.getElementById('orcamentoItensAdicionados');
const btnListarOrcamentos = document.getElementById('btnListarOrcamentos');
const listaOrcamentos = document.getElementById('listaOrcamentos');
//let itensOrcamentoRequest = []; let servicosCatalogo = [];

async function popularDropdownEdificacoesParaOrcamento() {
     try {
        const response = await fetch(API_URL_EDIFICACOES); const edificacoes = await handleFetchResponse(response);
        if (!selectOrcamentoEdificacao) { console.warn("Dropdown 'orcamentoEdificacaoId' não encontrado."); return; }
        selectOrcamentoEdificacao.innerHTML = '<option value="">-- Edificação --</option>';
        if (Array.isArray(edificacoes) && edificacoes.length > 0) {
            edificacoes.forEach(e => { selectOrcamentoEdificacao.innerHTML += `<option value="${e.idEdificacao}">${e.nome} (ID: ${e.idEdificacao})</option>`; });
        }
    } catch (error) { console.error('Erro ao popular edificações (orçamento):', error.message); if(selectOrcamentoEdificacao) selectOrcamentoEdificacao.innerHTML = '<option value="">Erro!</option>';}
}
async function popularDropdownTecnicosParaOrcamento() {
     try {
        const response = await fetch(API_URL_USUARIOS); const usuarios = await handleFetchResponse(response);
        if (!selectOrcamentoUsuario) { console.warn("Dropdown 'orcamentoUsuarioId' não encontrado."); return; }
        selectOrcamentoUsuario.innerHTML = '<option value="">-- Técnico --</option>';
        if (Array.isArray(usuarios) && usuarios.length > 0) {
            usuarios.forEach(u => { selectOrcamentoUsuario.innerHTML += `<option value="${u.id}">${u.nomeCompleto} (ID: ${u.id})</option>`; });
        }
    } catch (error) { console.error('Erro ao popular técnicos (orçamento):', error.message); if(selectOrcamentoUsuario) selectOrcamentoUsuario.innerHTML = '<option value="">Erro!</option>';}
}
async function popularDropdownServicos() {
    try {
        const response = await fetch(API_URL_SERVICOS); servicosCatalogo = await handleFetchResponse(response);
        if (!selectOrcamentoServico) { console.warn("Dropdown 'orcamentoServicoId' não encontrado."); return; }
        selectOrcamentoServico.innerHTML = '<option value="">-- Serviço --</option>';
         if (Array.isArray(servicosCatalogo) && servicosCatalogo.length > 0) {
            servicosCatalogo.forEach(s => { selectOrcamentoServico.innerHTML += `<option value="${s.idServico}">${s.nome} (R$ ${s.valorUnitario})</option>`; });
        }
    } catch (error) { console.error('Erro ao popular serviços:', error.message); if(selectOrcamentoServico) selectOrcamentoServico.innerHTML = '<option value="">Erro!</option>';}
}

btnAdicionarItem.addEventListener('click', () => {
    const idServico = selectOrcamentoServico.value;
    const quantidade = parseInt(inputOrcamentoQtd.value);
    if (!idServico || quantidade <= 0) { alert("Selecione serviço e quantidade válida."); return; }
    if (itensOrcamentoRequest.find(item => item.idServico == idServico)) { alert("Serviço já adicionado."); return; }
    itensOrcamentoRequest.push({ idServico: Number(idServico), quantidade: quantidade });
    atualizarListaItensUI();
    selectOrcamentoServico.value = ""; inputOrcamentoQtd.value = "1";
});
function atualizarListaItensUI() {
    if (itensOrcamentoRequest.length === 0) { listaItensAdicionadosDiv.innerHTML = '<p>Nenhum item.</p>'; return; }
    listaItensAdicionadosDiv.innerHTML = '';
    itensOrcamentoRequest.forEach((item, index) => {
        const servico = servicosCatalogo.find(s => s.idServico == item.idServico);
        const nomeServico = servico ? servico.nome : `Serviço ID ${item.idServico}`;
        listaItensAdicionadosDiv.innerHTML += `<div>${item.quantidade}x ${nomeServico} <button type="button" onclick="removerItemOrcamento(${index})">X</button></div>`;
    });
}
function removerItemOrcamento(index) { itensOrcamentoRequest.splice(index, 1); atualizarListaItensUI(); }

formOrcamento.addEventListener('submit', async (e) => {
    e.preventDefault();
    const orcamentoRequest = {
        idEdificacao: document.getElementById('orcamentoEdificacaoId').value, idUsuario: document.getElementById('orcamentoUsuarioId').value,
        dataValidade: document.getElementById('orcamentoValidade').value, itens: itensOrcamentoRequest
    };
    if (itensOrcamentoRequest.length === 0) { alert("Adicione itens."); return; }
    if (!orcamentoRequest.idEdificacao || !orcamentoRequest.idUsuario || !orcamentoRequest.dataValidade) { alert("Preencha Edificação, Técnico e Validade."); return; }
    try {
        orcamentoRequest.idEdificacao = Number(orcamentoRequest.idEdificacao); orcamentoRequest.idUsuario = Number(orcamentoRequest.idUsuario);
        const response = await fetch(API_URL_ORCAMENTOS, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(orcamentoRequest) });
        if (response.ok) {
            const dto = await handleFetchResponse(response); alert(`Orçamento ${dto.idOrcamento} criado. Valor: R$ ${dto.valorTotal}`);
            formOrcamento.reset(); itensOrcamentoRequest = []; atualizarListaItensUI(); listarOrcamentos(); popularDropdownOrcamentosPendentes();
        } else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro fatal: ' + error.message); console.error(error); }
});

async function listarOrcamentos() {
    try {
        const response = await fetch(API_URL_ORCAMENTOS); const orcamentos = await handleFetchResponse(response);
        listaOrcamentos.innerHTML = '<h3>Orçamentos:</h3>';
        if (!Array.isArray(orcamentos)) throw new Error("Resposta inválida");
        if (orcamentos.length === 0) { listaOrcamentos.innerHTML += '<p>Nenhum.</p>'; }
        else { orcamentos.forEach(o => {
            const texto = `ID=${o.idOrcamento}, Status=${o.status}, Valor=R$ ${o.valorTotal}, Edif=${o.nomeEdificacao}`;
            let botoes = ''; if (o.status === 'Pendente') { botoes = `<button class="btn-recusar-orcamento" data-id="${o.idOrcamento}">Recusar</button> <button class="btn-excluir-orcamento" data-id="${o.idOrcamento}">Excluir</button>`; }
            listaOrcamentos.innerHTML += `<div>${texto} ${botoes}</div>`;
         }); adicionarListenersBotoesOrcamento(); }
    } catch (error) { alert('Erro ao listar orçamentos: ' + error.message); console.error(error); listaOrcamentos.innerHTML += '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarOrcamentos.addEventListener('click', listarOrcamentos);

async function recusarOrcamento(id) {
     if (!confirm(`Recusar orçamento ID ${id}?`)) return;
     try {
         const response = await fetch(`${API_URL_ORCAMENTOS}/${id}/status`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ status: 'Recusado' }) });
         if (response.ok) { alert('Orçamento recusado!'); listarOrcamentos(); popularDropdownOrcamentosPendentes(); }
         else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
     } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
async function deletarOrcamento(id) {
    if (!confirm(`Excluir orçamento ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_ORCAMENTOS}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) { alert('Orçamento excluído!'); listarOrcamentos(); popularDropdownOrcamentosPendentes();}
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesOrcamento() {
    document.querySelectorAll('.btn-recusar-orcamento').forEach(b => b.replaceWith(b.cloneNode(true))); document.querySelectorAll('.btn-excluir-orcamento').forEach(b => b.replaceWith(b.cloneNode(true)));
    document.querySelectorAll('.btn-recusar-orcamento').forEach(b => b.addEventListener('click', (e) => recusarOrcamento(e.target.getAttribute('data-id'))));
    document.querySelectorAll('.btn-excluir-orcamento').forEach(b => b.addEventListener('click', (e) => deletarOrcamento(e.target.getAttribute('data-id'))));
}


// --- ORDENS DE SERVIÇO ---
const formAprovarOrcamento = document.getElementById('formAprovarOrcamento');
const selectAprovarOrcamento = document.getElementById('aprovarOrcamentoId');
const inputOSDataExecucao = document.getElementById('osDataExecucao');
const btnListarOS = document.getElementById('btnListarOS');
const listaOrdensServico = document.getElementById('listaOrdensServico');

async function popularDropdownOrcamentosPendentes() {
     try {
        const response = await fetch(API_URL_ORCAMENTOS); const orcamentos = await handleFetchResponse(response);
        if (!selectAprovarOrcamento) { console.warn("Dropdown 'aprovarOrcamentoId' não encontrado."); return; }
        selectAprovarOrcamento.innerHTML = '<option value="">-- Orçamento Pendente --</option>';
         if (Array.isArray(orcamentos)) {
            orcamentos.forEach(o => { if (o.status === "Pendente") { selectAprovarOrcamento.innerHTML += `<option value="${o.idOrcamento}">ID: ${o.idOrcamento} (${o.nomeEdificacao} R$ ${o.valorTotal})</option>`; } });
        }
    } catch (error) { console.error('Erro ao popular orçamentos pendentes:', error.message); if(selectAprovarOrcamento) selectAprovarOrcamento.innerHTML = '<option value="">Erro!</option>';}
}

formAprovarOrcamento.addEventListener('submit', async (e) => {
    e.preventDefault();
    const idOrcamento = selectAprovarOrcamento.value; const dataExecucao = inputOSDataExecucao.value;
    if (!idOrcamento || !dataExecucao) { alert("Selecione orçamento e data."); return; }
    try {
        const response = await fetch(`${API_URL_ORDENS_SERVICO}/aprovar-orcamento/${idOrcamento}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ dataExecucaoPrevista: dataExecucao }) });
        if (response.ok) {
            const dto = await handleFetchResponse(response); alert(`OS ${dto.idOrdemServico} gerada.`);
            formAprovarOrcamento.reset(); listarOrdensServico(); listarOrcamentos(); popularDropdownOrcamentosPendentes();
        } else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro fatal: ' + error.message); console.error(error); }
});

async function listarOrdensServico() {
    try {
        const response = await fetch(API_URL_ORDENS_SERVICO); const ordens = await handleFetchResponse(response);
        listaOrdensServico.innerHTML = '<h3>Ordens de Serviço:</h3>';
         if (!Array.isArray(ordens)) throw new Error("Resposta inválida");
        if (ordens.length === 0) { listaOrdensServico.innerHTML += '<p>Nenhuma.</p>'; }
        else { ordens.forEach(os => {
            const texto = `ID=${os.idOrdemServico}, OrcID=${os.idOrcamento} (${os.nomeEdificacaoOrcamento}), Status=${os.statusServico}, Previsto=${os.dataExecucaoPrevista}`;
            let botoes = '';
            if (os.statusServico !== 'Concluído' && os.statusServico !== 'Cancelado') {
                 botoes = `<select class="select-status-os" data-id="${os.idOrdemServico}">
                               <option value="">Mudar Status...</option>
                               <option value="Em Andamento"${os.statusServico === 'Em Andamento' ? ' selected' : ''}>Em Andamento</option>
                               <option value="Concluído"${os.statusServico === 'Concluído' ? ' selected' : ''}>Concluído</option>
                           </select>
                           <button class="btn-cancelar-os" data-id="${os.idOrdemServico}">Cancelar OS</button>`;
            }
            listaOrdensServico.innerHTML += `<div>${texto} ${botoes}</div>`;
         }); adicionarListenersBotoesOS(); }
    } catch (error) { alert('Erro ao listar OS: ' + error.message); console.error(error); listaOrdensServico.innerHTML += '<p style="color:red;">Erro ao carregar.</p>';}
}
btnListarOS.addEventListener('click', listarOrdensServico);

async function atualizarStatusOS(id, novoStatus) {
     if (!novoStatus) { listarOrdensServico(); return; }
     if (!confirm(`Mudar status da OS ID ${id} para "${novoStatus}"?`)) { listarOrdensServico(); return; }
     try {
         const response = await fetch(`${API_URL_ORDENS_SERVICO}/${id}/status`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ status: novoStatus }) });
         if (response.ok) { alert('Status da OS atualizado!'); listarOrdensServico(); }
         else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); listarOrdensServico(); }
     } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); listarOrdensServico(); }
}
async function cancelarOS(id) {
    if (!confirm(`Cancelar OS ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_URL_ORDENS_SERVICO}/${id}/cancelar`, { method: 'POST' });
        if (response.ok) { alert('OS Cancelada!'); listarOrdensServico(); }
        else { const errorMsg = await response.text(); alert(`Erro: ${errorMsg}`); }
    } catch (error) { alert('Erro ao conectar: ' + error.message); console.error(error); }
}
function adicionarListenersBotoesOS() {
    document.querySelectorAll('.select-status-os').forEach(select => {
        const newSelect = select.cloneNode(true);
        select.parentNode.replaceChild(newSelect, select);
        newSelect.addEventListener('change', (e) => {
            const id = e.target.getAttribute('data-id');
            const novoStatus = e.target.value;
            atualizarStatusOS(id, novoStatus);
        });
    });
    document.querySelectorAll('.btn-cancelar-os').forEach(b => b.replaceWith(b.cloneNode(true)));
    document.querySelectorAll('.btn-cancelar-os').forEach(b => b.addEventListener('click', (e) => cancelarOS(e.target.getAttribute('data-id'))));
}


// --- INICIALIZAÇÃO ---
function inicializarPagina() {
    console.log("Inicializando página...");
    listarUsuarios();
    listarClientes();
    popularDropdownClientes();
    listarEdificacoes();
    popularDropdownEdificacoesParaEquipamento();
    listarEquipamentos();
    popularDropdownEdificacoesParaInspecao();
    popularDropdownTecnicos();
    listarInspecoes();
    popularDropdownInspecoes();
    popularDropdownEquipamentos();
    listarServicos();
    popularDropdownEdificacoesParaOrcamento();
    popularDropdownTecnicosParaOrcamento();
    popularDropdownServicos();
    listarOrcamentos();
    popularDropdownOrcamentosPendentes();
    listarOrdensServico();
    console.log("Inicialização concluída.");
}

document.addEventListener('DOMContentLoaded', inicializarPagina);