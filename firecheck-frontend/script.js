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
const API_URL_RELATORIOS = 'http://localhost:8080/api/relatorios';
const API_URL_DASHBOARD = 'http://localhost:8080/api/dashboard';

// --- LOGIN CHECK & PERMISSÕES ---
const usuarioLogadoStr = sessionStorage.getItem('usuarioLogado');

if (!usuarioLogadoStr && !window.location.href.endsWith('login.html')) {
    window.location.href = 'login.html';
}

function aplicarPermissoes() {
    if (usuarioLogadoStr) {
        const user = JSON.parse(usuarioLogadoStr);
        console.log(`Usuário logado: ${user.nomeCompleto} (${user.perfil})`);
        
        if (user.perfil !== 'ADMIN') {
            const cardUsers = document.getElementById('cardGestaoUsuarios');
            if (cardUsers) cardUsers.style.display = 'none';
            const cardServicos = document.getElementById('cardGestaoServicos');
            if (cardServicos) cardServicos.style.display = 'none';
        }
    }
}

const btnLogout = document.getElementById('btnLogout');
if (btnLogout) {
    btnLogout.addEventListener('click', () => {
        sessionStorage.removeItem('usuarioLogado');
        window.location.href = 'login.html';
    });
}

// Variáveis de Estado
let idClienteEditando = null;
let idUsuarioEditando = null;
let idEdificacaoEditando = null;
let idEquipamentoEditando = null;
let idInspecaoEditando = null;
let itemInspecEditando = null; // Guarda {idInspecao, idEquipamento}
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

// --- FUNÇÕES DE DROPDOWN ---
async function popularDropdownClientes() {
    try {
        const clientes = await handleFetchResponse(await fetch(API_URL_CLIENTES));
        const select = document.getElementById('edificacaoClienteId');
        if (select && Array.isArray(clientes)) {
            select.innerHTML = '<option value="">-- Selecione um Cliente --</option>';
            clientes.forEach(c => select.innerHTML += `<option value="${c.id}">${c.razaoSocial}</option>`);
        }
    } catch (e) {}
}

async function popularDropdownTecnicos() {
    try {
        const usuarios = await handleFetchResponse(await fetch(API_URL_USUARIOS));
        const select = document.getElementById('inspecaoTecnicoId');
        if (select && Array.isArray(usuarios)) {
            select.innerHTML = '<option value="">-- Selecione um Técnico --</option>';
            usuarios.forEach(u => select.innerHTML += `<option value="${u.id}">${u.nomeCompleto}</option>`);
        }
    } catch (e) {}
}

async function popularDropdownTecnicosParaOrcamento() {
    try {
        const usuarios = await handleFetchResponse(await fetch(API_URL_USUARIOS));
        const select = document.getElementById('orcamentoUsuarioId');
        if (select && Array.isArray(usuarios)) {
            select.innerHTML = '<option value="">-- Selecione um Técnico --</option>';
            usuarios.forEach(u => select.innerHTML += `<option value="${u.id}">${u.nomeCompleto}</option>`);
        }
    } catch (e) {}
}

async function popularDropdownEdificacoesParaEquipamento() {
    try {
        const edificacoes = await handleFetchResponse(await fetch(API_URL_EDIFICACOES));
        const select = document.getElementById('equipamentoEdificacaoId');
        if (select && Array.isArray(edificacoes)) {
            select.innerHTML = '<option value="">-- Selecione uma Edificação --</option>';
            edificacoes.forEach(e => select.innerHTML += `<option value="${e.idEdificacao}">${e.nome}</option>`);
        }
    } catch (e) {}
}

async function popularDropdownEdificacoesParaInspecao() {
    try {
        const edificacoes = await handleFetchResponse(await fetch(API_URL_EDIFICACOES));
        const select = document.getElementById('inspecaoEdificacaoId');
        if (select && Array.isArray(edificacoes)) {
            select.innerHTML = '<option value="">-- Selecione uma Edificação --</option>';
            edificacoes.forEach(e => select.innerHTML += `<option value="${e.idEdificacao}">${e.nome}</option>`);
        }
    } catch (e) {}
}

async function popularDropdownEdificacoesParaOrcamento() {
    try {
        const edificacoes = await handleFetchResponse(await fetch(API_URL_EDIFICACOES));
        const select = document.getElementById('orcamentoEdificacaoId');
        if (select && Array.isArray(edificacoes)) {
            select.innerHTML = '<option value="">-- Selecione uma Edificação --</option>';
            edificacoes.forEach(e => select.innerHTML += `<option value="${e.idEdificacao}">${e.nome}</option>`);
        }
    } catch (e) {}
}

async function popularDropdownEquipamentos() {
    try {
        const equips = await handleFetchResponse(await fetch(API_URL_EQUIPAMENTOS));
        ['itemEquipamentoId', 'ncEquipamentoId'].forEach(id => {
            const sel = document.getElementById(id);
            if (sel && Array.isArray(equips)) {
                sel.innerHTML = '<option value="">-- Selecione Equipamento --</option>';
                equips.forEach(e => sel.innerHTML += `<option value="${e.idEquipamento}">${e.tipoEquipamento} (ID:${e.idEquipamento})</option>`);
            }
        });
    } catch (e) {}
}

async function popularDropdownInspecoes() {
    try {
        const inspecoes = await handleFetchResponse(await fetch(API_URL_INSPECOES));
        ['itemInspecaoId', 'filtroInspecaoId', 'ncInspecaoId', 'filtroNCInspecaoId'].forEach(id => {
            const sel = document.getElementById(id);
            if (sel && Array.isArray(inspecoes)) {
                sel.innerHTML = id.includes('filtro') ? '<option value="">-- Filtrar --</option>' : '<option value="">-- Selecione Inspeção --</option>';
                inspecoes.forEach(i => sel.innerHTML += `<option value="${i.idInspecao}">ID:${i.idInspecao} (${i.nomeEdificacao})</option>`);
            }
        });
    } catch (e) {}
}

async function popularDropdownServicos() {
    try {
        servicosCatalogo = await handleFetchResponse(await fetch(API_URL_SERVICOS));
        const select = document.getElementById('orcamentoServicoId');
        if (select && Array.isArray(servicosCatalogo)) {
            select.innerHTML = '<option value="">-- Selecione Serviço --</option>';
            servicosCatalogo.forEach(s => select.innerHTML += `<option value="${s.idServico}">${s.nome} (R$${s.valorUnitario})</option>`);
        }
    } catch (e) {}
}

async function popularDropdownOrcamentosPendentes() {
    try {
        const orcs = await handleFetchResponse(await fetch(API_URL_ORCAMENTOS));
        const select = document.getElementById('aprovarOrcamentoId');
        if (select && Array.isArray(orcs)) {
            select.innerHTML = '<option value="">-- Orçamento Pendente --</option>';
            orcs.forEach(o => {
                if (o.status === 'Pendente') {
                    select.innerHTML += `<option value="${o.idOrcamento}">ID:${o.idOrcamento} (${o.nomeEdificacao} - R$${o.valorTotal})</option>`;
                }
            });
        }
    } catch (e) {}
}

async function verificarAlertasEstoque() {
    try {
        const alertas = await handleFetchResponse(await fetch(`${API_URL_SERVICOS}/alertas`));
        const div = document.getElementById('areaAlertas');
        const ul = document.getElementById('listaAlertasEstoque');
        if (div && ul) {
            ul.innerHTML = '';
            if (Array.isArray(alertas) && alertas.length > 0) {
                div.style.display = 'block';
                alertas.forEach(s => ul.innerHTML += `<li>${s.nome}: Atual ${s.estoque} (Mín ${s.estoqueMinimo})</li>`);
            } else {
                div.style.display = 'none';
            }
        }
    } catch (e) {}
}

// --- FUNÇÃO DO DASHBOARD (CORRIGIDA COM ANTI-CACHE) ---
async function carregarDashboard() {
    try {
        // Adicionamos um timestamp (?t=...) para enganar o navegador e forçar uma nova busca
        const timestamp = new Date().getTime();
        const response = await fetch(`${API_URL_DASHBOARD}?t=${timestamp}`, {
            method: 'GET',
            headers: {
                'Cache-Control': 'no-cache',
                'Pragma': 'no-cache'
            }
        });
        
        const d = await handleFetchResponse(response);
        
        if (d) {
            if (document.getElementById('dashClientes')) document.getElementById('dashClientes').innerText = d.totalClientes;
            if (document.getElementById('dashTecnicos')) document.getElementById('dashTecnicos').innerText = d.totalTecnicos;
            if (document.getElementById('dashEdificacoes')) document.getElementById('dashEdificacoes').innerText = d.totalEdificacoes;
            if (document.getElementById('dashEquipamentos')) document.getElementById('dashEquipamentos').innerText = d.totalEquipamentos;
            if (document.getElementById('dashOrcamentos')) document.getElementById('dashOrcamentos').innerText = d.orcamentosPendentes;
            if (document.getElementById('dashOS')) document.getElementById('dashOS').innerText = d.osEmAberto;
            if (document.getElementById('dashAlertas')) document.getElementById('dashAlertas').innerText = d.alertasEstoque;
        }
    } catch (e) { console.error("Erro no Dashboard:", e); }
}

// ============================================================
// LISTAGENS E CRUD
// ============================================================

// 1. USUARIOS
const formUsuario = document.getElementById('formUsuario');
const btnListarUsuarios = document.getElementById('btnListarUsuarios');
const listaUsuarios = document.getElementById('listaUsuarios');

formUsuario.addEventListener('submit', async (e) => {
    e.preventDefault();
    const u = {
        nomeCompleto: document.getElementById('usuarioNome').value, cpf: document.getElementById('usuarioCpf').value,
        email: document.getElementById('usuarioEmail').value, telefone: document.getElementById('usuarioTelefone').value,
        login: document.getElementById('usuarioLogin').value, senha: document.getElementById('usuarioSenha').value,
        perfil: document.getElementById('usuarioPerfil').value
    };
    if (idUsuarioEditando && !u.senha) delete u.senha;
    const url = idUsuarioEditando ? `${API_URL_USUARIOS}/${idUsuarioEditando}` : API_URL_USUARIOS;
    const method = idUsuarioEditando ? 'PUT' : 'POST';
    try {
        const res = await fetch(url, { method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(u) });
        const txt = await res.text();
        let msg = res.ok ? "Sucesso!" : "Erro: " + txt;
        if(res.ok && method==='PUT') { try { const obj = JSON.parse(txt); msg = `Usuário ID ${obj.id} atualizado!`; } catch(e){} }
        alert(msg);
        if (res.ok) { idUsuarioEditando = null; document.getElementById('formUsuario').reset(); 
        document.getElementById('formUsuario').querySelector('button').textContent = 'Cadastrar Usuário';
        listarUsuarios(); popularDropdownTecnicos(); popularDropdownTecnicosParaOrcamento(); carregarDashboard(); }
    } catch (e) { alert(e.message); }
});

async function listarUsuarios() {
    try {
        const users = await handleFetchResponse(await fetch(API_URL_USUARIOS));
        listaUsuarios.innerHTML = '<h3>Usuários:</h3>';
        if (Array.isArray(users)) {
            if(users.length===0) listaUsuarios.innerHTML += '<p>Nenhum.</p>';
            users.forEach(u => listaUsuarios.innerHTML += `<div>ID=${u.id}, Nome=${u.nomeCompleto} [${u.perfil}] <button onclick="editUser(${u.id})">Editar</button><button onclick="delUser(${u.id})">Excluir</button></div>`);
        }
    } catch(e){}
}
document.getElementById('btnListarUsuarios').addEventListener('click', listarUsuarios);
window.editUser = async (id) => {
    const u = await handleFetchResponse(await fetch(`${API_URL_USUARIOS}/${id}`));
    document.getElementById('usuarioNome').value = u.nomeCompleto; document.getElementById('usuarioCpf').value = u.cpf;
    document.getElementById('usuarioEmail').value = u.email; document.getElementById('usuarioTelefone').value = u.telefone;
    document.getElementById('usuarioLogin').value = u.login; document.getElementById('usuarioPerfil').value = u.perfil;
    document.getElementById('usuarioSenha').value = ''; idUsuarioEditando = id;
    document.getElementById('formUsuario').querySelector('button').textContent = 'Salvar Alterações';
};
window.delUser = async (id) => { if (confirm('Excluir?')) { await handleFetchResponse(await fetch(`${API_URL_USUARIOS}/${id}`, { method: 'DELETE' })); listarUsuarios(); } };

// 2. CLIENTES
const formCliente = document.getElementById('formCliente');
const btnListarClientes = document.getElementById('btnListarClientes');
const listaClientes = document.getElementById('listaClientes');

formCliente.addEventListener('submit', async (e) => {
    e.preventDefault();
    const c = {
        razaoSocial: document.getElementById('clienteRazao').value, cnpjCpf: document.getElementById('clienteCnpjCpf').value,
        endereco: document.getElementById('clienteEndereco').value, telefone: document.getElementById('clienteTelefone').value,
        responsavel: document.getElementById('clienteResponsavel').value, email: document.getElementById('clienteEmail').value
    };
    const url = idClienteEditando ? `${API_URL_CLIENTES}/${idClienteEditando}` : API_URL_CLIENTES;
    const method = idClienteEditando ? 'PUT' : 'POST';
    try {
        const res = await fetch(url, { method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(c) });
        const txt = await res.text();
        let msg = res.ok ? "Sucesso!" : "Erro: " + txt;
        if(res.ok && method==='PUT') { try { const obj = JSON.parse(txt); msg = `Cliente ID ${obj.id} atualizado!`; } catch(e){} }
        alert(msg);
        if (res.ok) { idClienteEditando = null; document.getElementById('formCliente').reset(); 
        document.getElementById('formCliente').querySelector('button').textContent = 'Cadastrar Cliente';
        listarClientes(); popularDropdownClientes(); carregarDashboard(); }
    } catch (e) { alert(e.message); }
});
async function listarClientes() {
    try {
        const clients = await handleFetchResponse(await fetch(API_URL_CLIENTES));
        listaClientes.innerHTML = '<h3>Clientes:</h3>';
        if(Array.isArray(clients)) {
             if(clients.length===0) listaClientes.innerHTML += '<p>Nenhum.</p>';
             clients.forEach(c => listaClientes.innerHTML += `<div>ID=${c.id}, ${c.razaoSocial} <button onclick="editClient(${c.id})">Editar</button><button onclick="delClient(${c.id})">Excluir</button></div>`);
        }
    } catch (e) {}
}
document.getElementById('btnListarClientes').addEventListener('click', listarClientes);
window.editClient = async (id) => {
    const c = await handleFetchResponse(await fetch(`${API_URL_CLIENTES}/${id}`));
    document.getElementById('clienteRazao').value = c.razaoSocial; document.getElementById('clienteCnpjCpf').value = c.cnpjCpf;
    document.getElementById('clienteEndereco').value = c.endereco; document.getElementById('clienteTelefone').value = c.telefone;
    document.getElementById('clienteResponsavel').value = c.responsavel; document.getElementById('clienteEmail').value = c.email;
    idClienteEditando = id; document.getElementById('formCliente').querySelector('button').textContent = 'Salvar Alterações';
};
window.delClient = async (id) => { if(confirm('Excluir?')) { await handleFetchResponse(await fetch(`${API_URL_CLIENTES}/${id}`, {method:'DELETE'})); listarClientes(); popularDropdownClientes(); } };

// 3. EDIFICACOES
const formEdificacao = document.getElementById('formEdificacao');
const selectClienteEdificacao = document.getElementById('edificacaoClienteId');
const btnListarEdificacoes = document.getElementById('btnListarEdificacoes');
const listaEdificacoes = document.getElementById('listaEdificacoes');

formEdificacao.addEventListener('submit', async (e) => {
    e.preventDefault();
    const p = { nome: document.getElementById('edificacaoNome').value, endereco: document.getElementById('edificacaoEndereco').value, cep: document.getElementById('edificacaoCep').value, idCliente: document.getElementById('edificacaoClienteId').value };
    if(!p.idCliente) return alert('Selecione Cliente');
    const url = idEdificacaoEditando ? `${API_URL_EDIFICACOES}/${idEdificacaoEditando}` : API_URL_EDIFICACOES;
    const method = idEdificacaoEditando ? 'PUT' : 'POST';
    try {
        const res = await fetch(url, { method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(p) });
        const txt = await res.text();
        if(res.ok) { alert("Sucesso!"); idEdificacaoEditando = null; document.getElementById('formEdificacao').reset(); document.getElementById('formEdificacao').querySelector('button').textContent = 'Cadastrar Edificação';
        listarEdificacoes(); popularDropdownEdificacoesParaEquipamento(); popularDropdownEdificacoesParaInspecao(); popularDropdownEdificacoesParaOrcamento(); }
        else alert(txt);
    } catch(e) { alert(e.message); }
});
async function listarEdificacoes() {
    try {
        const eds = await handleFetchResponse(await fetch(API_URL_EDIFICACOES));
        listaEdificacoes.innerHTML = '<h3>Edificações:</h3>';
        if(Array.isArray(eds)) eds.forEach(e => listaEdificacoes.innerHTML += `<div>${e.nome} <button onclick="editEdif(${e.idEdificacao})">Editar</button><button onclick="delEdif(${e.idEdificacao})">Excluir</button></div>`);
    } catch (e) { console.error(e); }
}
document.getElementById('btnListarEdificacoes').addEventListener('click', listarEdificacoes);
window.editEdif = async (id) => {
    const e = await handleFetchResponse(await fetch(`${API_URL_EDIFICACOES}/${id}`));
    document.getElementById('edificacaoNome').value = e.nome; document.getElementById('edificacaoEndereco').value = e.endereco;
    document.getElementById('edificacaoCep').value = e.cep; document.getElementById('edificacaoClienteId').value = e.idCliente;
    idEdificacaoEditando = id; document.getElementById('formEdificacao').querySelector('button').textContent = 'Salvar Alterações';
};
window.delEdif = async (id) => { if (confirm('Excluir?')) { await handleFetchResponse(await fetch(`${API_URL_EDIFICACOES}/${id}`, { method: 'DELETE' })); listarEdificacoes(); } };

// 4. EQUIPAMENTOS
const formEquipamento = document.getElementById('formEquipamento');
const selectEdificacaoEquip = document.getElementById('equipamentoEdificacaoId');
const btnListarEquipamentos = document.getElementById('btnListarEquipamentos');
const listaEquipamentos = document.getElementById('listaEquipamentos');

formEquipamento.addEventListener('submit', async (e) => {
    e.preventDefault();
    const p = { tipoEquipamento: document.getElementById('equipamentoTipo').value, localizacao: document.getElementById('equipamentoLocal').value, dataFabricacao: document.getElementById('equipamentoFab').value, dataValidade: document.getElementById('equipamentoVal').value, idEdificacao: document.getElementById('equipamentoEdificacaoId').value };
    if(!p.idEdificacao) return alert("Selecione Edificação");
    const url = idEquipamentoEditando ? `${API_URL_EQUIPAMENTOS}/${idEquipamentoEditando}` : API_URL_EQUIPAMENTOS;
    const method = idEquipamentoEditando ? 'PUT' : 'POST';
    try {
        const res = await fetch(url, { method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(p) });
        if(res.ok) { alert("Sucesso!"); idEquipamentoEditando = null; document.getElementById('formEquipamento').reset(); document.getElementById('formEquipamento').querySelector('button').textContent = 'Cadastrar Equipamento'; listarEquipamentos(); popularDropdownEquipamentos(); }
        else alert(await res.text());
    } catch(e) { alert(e.message); }
});
async function listarEquipamentos() {
    try {
        const eqs = await handleFetchResponse(await fetch(API_URL_EQUIPAMENTOS));
        listaEquipamentos.innerHTML = '<h3>Equipamentos:</h3>';
        if(Array.isArray(eqs)) eqs.forEach(e => listaEquipamentos.innerHTML += `<div>${e.tipoEquipamento} <button onclick="editEquip(${e.idEquipamento})">Editar</button><button onclick="delEquip(${e.idEquipamento})">Excluir</button></div>`);
    } catch (e) { console.error(e); }
}
document.getElementById('btnListarEquipamentos').addEventListener('click', listarEquipamentos);
window.editEquip = async (id) => {
    const e = await handleFetchResponse(await fetch(`${API_URL_EQUIPAMENTOS}/${id}`));
    document.getElementById('equipamentoTipo').value = e.tipoEquipamento; document.getElementById('equipamentoLocal').value = e.localizacao;
    document.getElementById('equipamentoFab').value = e.dataFabricacao; document.getElementById('equipamentoVal').value = e.dataValidade;
    document.getElementById('equipamentoEdificacaoId').value = e.idEdificacao;
    idEquipamentoEditando = id; document.getElementById('formEquipamento').querySelector('button').textContent = 'Salvar Alterações';
};
window.delEquip = async (id) => { if(confirm('Excluir?')) { await handleFetchResponse(await fetch(`${API_URL_EQUIPAMENTOS}/${id}`, {method:'DELETE'})); listarEquipamentos(); } };

// 5. INSPEÇÕES
const formInspecao = document.getElementById('formInspecao');
const selectInspecaoEdificacao = document.getElementById('inspecaoEdificacaoId');
const selectInspecaoTecnico = document.getElementById('inspecaoTecnicoId');
const btnListarInspecoes = document.getElementById('btnListarInspecoes');
const listaInspecoes = document.getElementById('listaInspecoes');
const btnRelatorioInspecoes = document.getElementById('btnRelatorioInspecoes');
if(btnRelatorioInspecoes) btnRelatorioInspecoes.addEventListener('click', () => window.open(`${API_URL_RELATORIOS}/inspecoes/pdf`, '_blank'));
const btnRelatorioInspecoesExcel = document.getElementById('btnRelatorioInspecoesExcel');
if(btnRelatorioInspecoesExcel) btnRelatorioInspecoesExcel.addEventListener('click', () => window.open(`${API_URL_RELATORIOS}/inspecoes/excel`, '_blank'));

formInspecao.addEventListener('submit', async (e) => {
    e.preventDefault();
    const p = { dataInspecao: document.getElementById('inspecaoData').value, status: document.getElementById('inspecaoStatus').value, idEdificacao: document.getElementById('inspecaoEdificacaoId').value, idTecnico: document.getElementById('inspecaoTecnicoId').value };
    if(!p.idEdificacao || !p.idTecnico) return alert('Preencha tudo');
    const url = idInspecaoEditando ? `${API_URL_INSPECOES}/${idInspecaoEditando}` : API_URL_INSPECOES;
    const method = idInspecaoEditando ? 'PUT' : 'POST';
    try {
        const res = await fetch(url, { method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(p) });
        if(res.ok) { alert("Sucesso!"); idInspecaoEditando = null; document.getElementById('formInspecao').reset(); document.getElementById('formInspecao').querySelector('button').textContent = 'Agendar Inspeção'; listarInspecoes(); popularDropdownInspecoes(); }
        else alert(await res.text());
    } catch(e) { alert(e.message); }
});
async function listarInspecoes() {
    try {
        const is = await handleFetchResponse(await fetch(API_URL_INSPECOES));
        listaInspecoes.innerHTML = '<h3>Inspeções:</h3>';
        if(Array.isArray(is)) is.forEach(i => listaInspecoes.innerHTML += `<div>ID:${i.idInspecao} (${i.status}) <button onclick="editInsp(${i.idInspecao})">Editar</button><button onclick="delInsp(${i.idInspecao})">Excluir</button></div>`);
    } catch (e) { console.error(e); }
}
document.getElementById('btnListarInspecoes').addEventListener('click', listarInspecoes);
window.editInsp = async (id) => {
    const i = await handleFetchResponse(await fetch(`${API_URL_INSPECOES}/${id}`));
    document.getElementById('inspecaoData').value = i.dataInspecao ? i.dataInspecao.substring(0,16) : '';
    document.getElementById('inspecaoStatus').value = i.status; document.getElementById('inspecaoEdificacaoId').value = i.idEdificacao;
    document.getElementById('inspecaoTecnicoId').value = i.idTecnico; idInspecaoEditando = id; document.getElementById('formInspecao').querySelector('button').textContent = 'Salvar Alterações';
};
window.delInsp = async (id) => { if(confirm('Excluir?')) { await handleFetchResponse(await fetch(`${API_URL_INSPECOES}/${id}`, {method:'DELETE'})); listarInspecoes(); } };

// 6. ITENS, 7. NCs
document.getElementById('formItemInspecionado').addEventListener('submit', async(e)=>{ e.preventDefault(); const p = {idInspecao:document.getElementById('itemInspecaoId').value, idEquipamento:document.getElementById('itemEquipamentoId').value, statusGeral:document.getElementById('itemStatusGeral').value, observacoes:document.getElementById('itemObservacoes').value}; try{ await handleFetchResponse(await fetch(API_URL_ITENS,{method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(p)})); alert('Salvo!'); document.getElementById('formItemInspecionado').reset(); }catch(x){alert(x.message);} });
document.getElementById('btnListarItens').addEventListener('click', async()=>{ const id=document.getElementById('filtroInspecaoId').value; if(!id)return; const d=await handleFetchResponse(await fetch(`${API_URL_ITENS}/inspecao/${id}`)); document.getElementById('listaItensInspecionados').innerHTML=d.map(i=>`<div>${i.tipoEquipamento}: ${i.statusGeral}</div>`).join(''); });
document.getElementById('formNaoConformidade').addEventListener('submit', async(e)=>{ e.preventDefault(); const p = {idInspecao:document.getElementById('ncInspecaoId').value, idEquipamento:document.getElementById('ncEquipamentoId').value, descricao:document.getElementById('ncDescricao').value}; try{ await handleFetchResponse(await fetch(API_URL_NAOCONFORMIDADES,{method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(p)})); alert('Salvo!'); document.getElementById('formNaoConformidade').reset(); }catch(x){alert(x.message);} });
document.getElementById('btnListarNC').addEventListener('click', async()=>{ const id=document.getElementById('filtroNCInspecaoId').value; if(!id)return; const d=await handleFetchResponse(await fetch(`${API_URL_NAOCONFORMIDADES}/inspecao/${id}`)); document.getElementById('listaNaoConformidades').innerHTML=d.map(n=>`<div>${n.descricao}</div>`).join(''); });

// 8. SERVIÇOS
const formServico = document.getElementById('formServico');
const btnListarServicos = document.getElementById('btnListarServicos');
const listaServicos = document.getElementById('listaServicos');

formServico.addEventListener('submit', async (e) => {
    e.preventDefault();
    const s = { nome: document.getElementById('servicoNome').value, descricao: document.getElementById('servicoDescricao').value, valorUnitario: document.getElementById('servicoValor').value, tempoExecucaoHoras: document.getElementById('servicoTempo').value, estoque: document.getElementById('servicoEstoque').value, estoqueMinimo: document.getElementById('servicoEstoqueMinimo').value };
    const url = idServicoEditando ? `${API_URL_SERVICOS}/${idServicoEditando}` : API_URL_SERVICOS;
    const method = idServicoEditando ? 'PUT' : 'POST';
    try {
        const res = await fetch(url, { method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(s) });
        if (res.ok) { alert("Sucesso!"); idServicoEditando = null; document.getElementById('formServico').reset(); document.getElementById('formServico').querySelector('button').textContent = 'Cadastrar Serviço'; listarServicos(); popularDropdownServicos(); verificarAlertasEstoque(); carregarDashboard(); }
        else alert(await res.text());
    } catch(e) { alert(e.message); }
});
async function listarServicos() {
    try {
        const svs = await handleFetchResponse(await fetch(API_URL_SERVICOS));
        listaServicos.innerHTML = '<h3>Serviços:</h3>';
        if(Array.isArray(svs)) svs.forEach(s => listaServicos.innerHTML += `<div>${s.nome} (Estoque: ${s.estoque}) <button onclick="editServ(${s.idServico})">Editar</button><button onclick="delServ(${s.idServico})">Excluir</button></div>`);
    } catch (e) { console.error(e); }
}
document.getElementById('btnListarServicos').addEventListener('click', listarServicos);
window.editServ = async (id) => {
    const s = await handleFetchResponse(await fetch(`${API_URL_SERVICOS}/${id}`));
    document.getElementById('servicoNome').value = s.nome; document.getElementById('servicoDescricao').value = s.descricao;
    document.getElementById('servicoValor').value = s.valorUnitario; document.getElementById('servicoTempo').value = s.tempoExecucaoHoras;
    document.getElementById('servicoEstoque').value = s.estoque; document.getElementById('servicoEstoqueMinimo').value = s.estoqueMinimo;
    idServicoEditando = id; document.getElementById('formServico').querySelector('button').textContent = 'Salvar Alterações';
};
window.delServ = async (id) => { if(confirm('Excluir?')) { await handleFetchResponse(await fetch(`${API_URL_SERVICOS}/${id}`, {method:'DELETE'})); listarServicos(); } };

// 9. ORCAMENTOS & 10. OS
document.getElementById('btnAdicionarItemOrcamento').addEventListener('click', ()=>{ const id=document.getElementById('orcamentoServicoId').value; const qtd=document.getElementById('orcamentoItemQtd').value; if(id&&qtd>0){ itensOrcamentoRequest.push({idServico:Number(id), quantidade:Number(qtd)}); document.getElementById('orcamentoItensAdicionados').innerHTML+=`<div>Item ${id} Qtd ${qtd}</div>`; } });
document.getElementById('formOrcamento').addEventListener('submit', async(e)=>{ e.preventDefault(); const p = {idEdificacao:document.getElementById('orcamentoEdificacaoId').value, idUsuario:document.getElementById('orcamentoUsuarioId').value, dataValidade:document.getElementById('orcamentoValidade').value, itens:itensOrcamentoRequest}; try{ await handleFetchResponse(await fetch(API_URL_ORCAMENTOS,{method:'POST', headers:{'Content-Type':'application/json'},body:JSON.stringify(p)})); alert('Criado!'); document.getElementById('formOrcamento').reset(); itensOrcamentoRequest=[]; document.getElementById('orcamentoItensAdicionados').innerHTML=''; listarOrcamentos(); popularDropdownOrcamentosPendentes(); carregarDashboard(); }catch(x){alert(x.message);} });
async function listarOrcamentos() {
    try {
        const os = await handleFetchResponse(await fetch(API_URL_ORCAMENTOS));
        const div = document.getElementById('listaOrcamentos');
        div.innerHTML = '<h3>Orçamentos:</h3>';
        if(Array.isArray(os)) os.forEach(o => { let btns = o.status==='Pendente' ? `<button onclick="recusarOrcamento(${o.idOrcamento})">Recusar</button><button onclick="delOrc(${o.idOrcamento})">Excluir</button>` : ''; div.innerHTML += `<div>ID:${o.idOrcamento} ${o.status} ${btns}</div>`; });
    } catch(e) {}
}
document.getElementById('btnListarOrcamentos').addEventListener('click', listarOrcamentos);
window.delOrc = async (id) => { if (confirm('Excluir?')) { await handleFetchResponse(await fetch(`${API_URL_ORCAMENTOS}/${id}`, {method:'DELETE'})); listarOrcamentos(); } };
window.recusarOrcamento = async (id) => { if(confirm('Recusar?')) { await handleFetchResponse(await fetch(`${API_URL_ORCAMENTOS}/${id}/status`, {method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify({status:'Recusado'})})); listarOrcamentos(); } };

document.getElementById('formAprovarOrcamento').addEventListener('submit', async(e)=>{ e.preventDefault(); const id=document.getElementById('aprovarOrcamentoId').value; const dt=document.getElementById('osDataExecucao').value; try{ await handleFetchResponse(await fetch(`${API_URL_ORDENS_SERVICO}/aprovar-orcamento/${id}`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({dataExecucaoPrevista:dt})})); alert('OS Gerada!'); listarOS(); listarOrcamentos(); popularDropdownOrcamentosPendentes(); carregarDashboard(); }catch(x){alert(x.message);} });
async function listarOS() {
    try {
        const oss = await handleFetchResponse(await fetch(API_URL_ORDENS_SERVICO));
        const div = document.getElementById('listaOrdensServico');
        div.innerHTML = '<h3>OS:</h3>';
        if(Array.isArray(oss)) oss.forEach(o => {
            let btns = (o.statusServico !== 'Concluído' && o.statusServico !== 'Cancelado') ? `<button onclick="concluirOS(${o.idOrdemServico})">Concluir</button> <button onclick="cancelarOS(${o.idOrdemServico})">Cancelar</button>` : '';
            div.innerHTML += `<div>ID:${o.idOrdemServico} ${o.statusServico} ${btns}</div>`;
        });
    } catch(e) {}
}
document.getElementById('btnListarOS').addEventListener('click', listarOS);
window.concluirOS = async (id) => { if(confirm('Concluir?')) { await handleFetchResponse(await fetch(`${API_URL_ORDENS_SERVICO}/${id}/status`,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({status:'Concluído'})})); listarOS(); verificarAlertasEstoque(); carregarDashboard(); } };
window.cancelarOS = async (id) => { if(confirm('Cancelar?')) { await handleFetchResponse(await fetch(`${API_URL_ORDENS_SERVICO}/${id}/cancelar`,{method:'POST'})); listarOS(); } };

// INIT
document.addEventListener('DOMContentLoaded', () => {
    aplicarPermissoes();
    popularDropdownClientes(); popularDropdownTecnicos(); popularDropdownEdificacoesParaEquipamento();
    popularDropdownEdificacoesParaInspecao(); popularDropdownEdificacoesParaOrcamento(); popularDropdownEquipamentos();
    popularDropdownInspecoes(); popularDropdownServicos(); popularDropdownOrcamentosPendentes(); popularDropdownTecnicosParaOrcamento();
    listarUsuarios(); listarClientes(); listarEdificacoes(); listarEquipamentos(); listarInspecoes(); listarServicos(); listarOrcamentos(); listarOS();
    verificarAlertasEstoque(); carregarDashboard();
});