let editMode = false;
let produtoIdToEdit = null;
let produtoOriginal = null; // Para guardar os dados originais do produto em edição

window.onload = function () {
  const params = new URLSearchParams(window.location.search);
  produtoIdToEdit = params.get("id");

  if (produtoIdToEdit) {
    editMode = true;
    document.getElementById(
      "produtoFormPopupTitulo"
    ).textContent = `Editando Produto ID: ${produtoIdToEdit}`;
    document.getElementById("produtoIdFormPopup").value = produtoIdToEdit;

    document
      .getElementById("novoProdutoEstoqueDivPopup")
      .classList.add("hidden"); // Esconde campos de novo produto
    document.getElementById("edicaoProdutoDivPopup").classList.remove("hidden"); // Mostra campos de edição
    document.getElementById("btnSalvarProduto").textContent =
      "Salvar Alterações";

    fetchProductDetailsAndStock(produtoIdToEdit);
  } else {
    document.getElementById("produtoFormPopupTitulo").textContent =
      "Adicionar Novo Produto";
    document
      .getElementById("novoProdutoEstoqueDivPopup")
      .classList.remove("hidden");
    document.getElementById("edicaoProdutoDivPopup").classList.add("hidden");
    document.getElementById("btnSalvarProduto").textContent =
      "Salvar Novo Produto";
  }
};

async function fetchProductDetailsAndStock(id) {
  exibirResultadoPopup("Carregando dados do produto...", "loading");
  try {
    // Idealmente, ter um endpoint que retorne ProdutoEstoqueDTO por ID
    // Vamos usar /todosProdutosStatus e filtrar, como antes, mas agora pegando mais dados
    const response = await fetch("/todosProdutosStatus");
    if (!response.ok)
      throw new Error("Falha ao buscar lista de produtos para obter detalhes.");
    const produtos = await response.json();
    produtoOriginal = produtos.find((p) => p.id == id);

    if (produtoOriginal) {
      document.getElementById("produtoDescricaoPopup").value =
        produtoOriginal.descricao;
      document.getElementById("produtoPrecoPopup").value =
        produtoOriginal.precoUnitario;
      document.getElementById("produtoEstoqueAtualPopup").value =
        produtoOriginal.quantidadeEmEstoque;
      document.getElementById("produtoEstoqueMinEdicaoPopup").value =
        produtoOriginal.estoqueMin;
      document.getElementById("produtoEstoqueMaxEdicaoPopup").value =
        produtoOriginal.estoqueMax;
      atualizarDisplayStatusProduto(produtoOriginal.listado);
      exibirResultadoPopup("Dados carregados.", "success");
      setTimeout(
        () =>
          (document.getElementById("produtoFormPopupResult").style.display =
            "none"),
        1500
      );
    } else {
      throw new Error("Produto não encontrado para edição.");
    }
  } catch (error) {
    exibirResultadoPopup(
      "Falha ao carregar dados do produto: " + error.message,
      "error"
    );
    console.error(error);
  }
}

function atualizarDisplayStatusProduto(listado) {
  const statusSpan = document.getElementById("produtoStatusAtual");
  const btnRelistar = document.getElementById("btnRelistarProduto");
  const btnDelistar = document.getElementById("btnDelistarProduto");
  if (listado) {
    statusSpan.textContent = "LISTADO (Disponível para venda)";
    statusSpan.style.color = "green";
    btnRelistar.classList.add("active");
    btnDelistar.classList.remove("active");
    btnDelistar.classList.remove("inactive"); // Garante que não fique com as duas
  } else {
    statusSpan.textContent = "DE-LISTADO (Indisponível para venda)";
    statusSpan.style.color = "orange";
    btnDelistar.classList.add("active"); // Ou 'inactive' para visual
    btnDelistar.classList.add("inactive");
    btnRelistar.classList.remove("active");
  }
}

async function definirStatusProduto(listar) {
  if (!produtoIdToEdit || produtoOriginal == null) return;

  const acao = listar ? "re-listar" : "de-listar";
  const endpoint = listar
    ? `/produtos/${produtoIdToEdit}/relistar`
    : `/produtos/${produtoIdToEdit}`;
  const method = listar ? "POST" : "DELETE";

  if (
    !confirm(
      `Tem certeza que deseja ${acao} o produto "${produtoOriginal.descricao}" (ID: ${produtoIdToEdit})?`
    )
  ) {
    return;
  }

  exibirResultadoPopup(
    `${acao.charAt(0).toUpperCase() + acao.slice(1)}ndo produto...`,
    "loading"
  );
  try {
    const response = await fetch(endpoint, { method: method });
    if (!response.ok) {
      const errorData = await response.text(); // DELETE pode não retornar JSON
      throw new Error(
        `Erro ${response.status}: ${errorData || response.statusText}`
      );
    }
    exibirResultadoPopup(`Produto ${acao} com sucesso!`, "success");
    produtoOriginal.listado = listar; // Atualiza estado local
    atualizarDisplayStatusProduto(listar);
    notificarAlteracaoProduto();
  } catch (error) {
    exibirResultadoPopup(`Erro ao ${acao} produto: ${error.message}`, "error");
  }
}

async function submeterFormularioProdutoPopup() {
  const id = document.getElementById("produtoIdFormPopup").value;
  const descricao = document
    .getElementById("produtoDescricaoPopup")
    .value.trim();
  const precoStr = document.getElementById("produtoPrecoPopup").value;
  const quantidadeAumentarEstoqueStr = document.getElementById(
    "quantidadeAumentarEstoquePopup"
  ).value;

  if (!descricao) {
    exibirResultadoPopup("Descrição é obrigatória.", "error");
    return;
  }
  if (!precoStr) {
    exibirResultadoPopup("Preço é obrigatório.", "error");
    return;
  }
  const preco = parseFloat(precoStr);
  if (isNaN(preco) || preco < 0) {
    exibirResultadoPopup("Preço inválido.", "error");
    return;
  }

  let operacoesConcluidas = 0;
  let operacoesFalhas = 0;
  let mensagensSucesso = [];
  let mensagensErro = [];

  exibirResultadoPopup("Processando alterações...", "loading");

  // 1. Salvar alterações de Descrição e Preço (se houver)
  if (
    editMode &&
    id &&
    produtoOriginal &&
    (produtoOriginal.descricao !== descricao ||
      produtoOriginal.precoUnitario != preco)
  ) {
    try {
      const payloadEdicao = {
        id: parseInt(id),
        descricao,
        precoUnitario: preco,
      };
      const responseEdicao = await fetch(`/produtos/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payloadEdicao),
      });
      if (!responseEdicao.ok) {
        const errorData = await responseEdicao
          .json()
          .catch(() => ({ message: responseEdicao.statusText }));
        throw new Error(
          `Editar Desc/Preço - Erro ${responseEdicao.status}: ${errorData.message}`
        );
      }
      await responseEdicao.json();
      mensagensSucesso.push("Descrição/Preço atualizados.");
      operacoesConcluidas++;
      if (produtoOriginal) produtoOriginal.descricao = descricao; // Atualiza local
      if (produtoOriginal) produtoOriginal.precoUnitario = preco; // Atualiza local
    } catch (error) {
      mensagensErro.push(`Erro ao atualizar Descrição/Preço: ${error.message}`);
      operacoesFalhas++;
    }
  } else if (!editMode) {
    // É um Novo Produto
    const qtdIniStr = document.getElementById(
      "produtoEstoqueInicialPopup"
    ).value;
    const estMinStr = document.getElementById("produtoEstoqueMinPopup").value;
    const estMaxStr = document.getElementById("produtoEstoqueMaxPopup").value;

    const qtdIni = parseInt(qtdIniStr);
    const estMin = parseInt(estMinStr);
    const estMax = parseInt(estMaxStr);

    if (isNaN(qtdIni) || qtdIni < 0) {
      mensagensErro.push("Estoque inicial inválido.");
      operacoesFalhas++;
    }
    if (isNaN(estMin) || estMin < 0) {
      mensagensErro.push("Estoque mínimo inválido.");
      operacoesFalhas++;
    }
    if (isNaN(estMax) || estMax < estMin) {
      mensagensErro.push("Estoque máximo inválido (deve ser >= mínimo).");
      operacoesFalhas++;
    }

    if (operacoesFalhas === 0) {
      try {
        const payloadNovo = {
          descricao,
          precoUnitario: preco,
          quantidadeInicialEstoque: qtdIni,
          estoqueMin: estMin,
          estoqueMax: estMax,
        };
        const responseNovo = await fetch("/produtos", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payloadNovo),
        });
        if (!responseNovo.ok) {
          const errorData = await responseNovo
            .json()
            .catch(() => ({ message: responseNovo.statusText }));
          throw new Error(
            `Novo Produto - Erro ${responseNovo.status}: ${errorData.message}`
          );
        }
        const salvo = await responseNovo.json();
        mensagensSucesso.push(
          `Produto novo adicionado com sucesso! (ID: ${salvo.id})`
        );
        operacoesConcluidas++;
      } catch (error) {
        mensagensErro.push(`Erro ao adicionar novo produto: ${error.message}`);
        operacoesFalhas++;
      }
    }
  }

  // 2. Aumentar Estoque (se uma quantidade for especificada e for modo de edição)
  if (editMode && id && quantidadeAumentarEstoqueStr) {
    const quantidadeAumentar = parseInt(quantidadeAumentarEstoqueStr);
    if (!isNaN(quantidadeAumentar) && quantidadeAumentar > 0) {
      try {
        const responseEstoque = await fetch(
          `/produtos/${id}/entradaEstoque?qtdade=${quantidadeAumentar}`,
          { method: "POST" }
        );
        if (!responseEstoque.ok) {
          const errorData = await responseEstoque.text(); // Pode não ser JSON
          throw new Error(
            `Aumentar Estoque - Erro ${responseEstoque.status}: ${
              errorData || responseEstoque.statusText
            }`
          );
        }
        mensagensSucesso.push(
          `Estoque aumentado em ${quantidadeAumentar} unidades.`
        );
        operacoesConcluidas++;
        // Atualizar localmente se desejar
        if (produtoOriginal)
          produtoOriginal.quantidadeEmEstoque += quantidadeAumentar;
        document.getElementById("produtoEstoqueAtualPopup").value =
          produtoOriginal.quantidadeEmEstoque;
        document.getElementById("quantidadeAumentarEstoquePopup").value = 0; // Resetar campo
      } catch (error) {
        mensagensErro.push(`Erro ao aumentar estoque: ${error.message}`);
        operacoesFalhas++;
      }
    } else if (quantidadeAumentar < 0) {
      mensagensErro.push("Quantidade para aumentar estoque deve ser positiva.");
      operacoesFalhas++;
    }
  }

  // Finalizar e mostrar resultados
  if (operacoesFalhas > 0) {
    exibirResultadoPopup(
      "Falhas na operação:\n" +
        mensagensErro.join("\n") +
        (mensagensSucesso.length > 0
          ? "\nSucessos Parciais:\n" + mensagensSucesso.join("\n")
          : ""),
      "error"
    );
  } else if (operacoesConcluidas > 0) {
    exibirResultadoPopup(
      "Operações concluídas com sucesso:\n" + mensagensSucesso.join("\n"),
      "success"
    );
    notificarAlteracaoProduto(); // Notifica a janela principal sobre as alterações
    if (!editMode) {
      // Se foi um novo produto salvo com sucesso
      setTimeout(() => window.close(), 1500);
    }
  } else {
    exibirResultadoPopup(
      "Nenhuma alteração detectada ou operação realizada.",
      "loading"
    ); // Ou 'normal'
    setTimeout(
      () =>
        (document.getElementById("produtoFormPopupResult").style.display =
          "none"),
      2000
    );
  }
}

function notificarAlteracaoProduto() {
  if (
    window.opener &&
    typeof window.opener.notificarProdutoSalvo === "function"
  ) {
    window.opener.notificarProdutoSalvo(); // Função na janela principal para recarregar a lista
  }
}

function exibirResultadoPopup(message, type = "success") {
  const el = document.getElementById("produtoFormPopupResult");
  el.style.display = "block";
  el.innerHTML = message.replace(/\n/g, "<br>"); // Troca \n por <br> para exibição no HTML
  el.className = "result ";
  if (type === "error") el.classList.add("error");
  else if (type === "loading") el.classList.add("loading");
  else el.classList.add("success");
}
async function popularSelectNomesClientes(selectId) {
  const selectCliente = document.getElementById(selectId);
  if (!selectCliente) return;

  selectCliente.innerHTML =
    '<option value="">-- Selecione um Cliente --</option>'; // Opção padrão

  try {
    const response = await fetch("/gerencial/clientesComCompras");
    const nomesClientes = await handleFetchError(response); // Sua função de tratamento de erro

    if (nomesClientes && nomesClientes.length > 0) {
      nomesClientes.forEach((nome) => {
        const option = document.createElement("option");
        option.value = nome; // O valor será o próprio nome do cliente
        option.textContent = nome;
        selectCliente.appendChild(option);
      });
    } else {
      selectCliente.innerHTML =
        '<option value="">Nenhum cliente com compras encontrado</option>';
    }
  } catch (error) {
    console.error("Erro ao popular select de nomes de clientes:", error);
    selectCliente.innerHTML =
      '<option value="">Erro ao carregar clientes</option>';
  }
}

async function popularSelectProdutos(selectId) {
  const selectProduto = document.getElementById(selectId);
  if (!selectProduto) return;

  selectProduto.innerHTML = '<option value="">-- Todos os Produtos --</option>'; // Opção para buscar todos

  try {
    const response = await fetch("/todosProdutosStatus");
    const produtos = await handleFetchError(response);

    if (produtos && produtos.length > 0) {
      produtos.forEach((produto) => {
        const option = document.createElement("option");
        option.value = produto.id;
        option.textContent = `${produto.id} - ${produto.descricao}`;
        selectProduto.appendChild(option);
      });
    }
  } catch (error) {
    console.error("Erro ao popular select de produtos:", error);
  }
}

function renderizarFiltrosConsultaAvancada() {
  const tipoConsulta = document.getElementById(
    "tipoConsultaGerencialAvancada"
  ).value;
  const containerFiltros = document.getElementById(
    "filtrosConsultaAvancadaContainer"
  );
  containerFiltros.innerHTML = ""; // Limpa filtros anteriores

  // Helper para criar inputs de data (AAAA-MM-DD)
  const criarInputData = (id, label, obrigatorio = true) => {
    const hoje = new Date().toISOString().split("T")[0]; // Pega a data de hoje
    return `
                    <div style="margin-bottom:10px;">
                        <label for="${id}" style="display:block; margin-bottom:3px;">${label}${
      obrigatorio ? "*" : ""
    }:</label>
                        <input type="date" id="${id}" ${
      obrigatorio ? "required" : ""
    } value="${id.toLowerCase().includes("final") ? hoje : ""}">
                    </div>
                `;
  };

  // Helper para criar input de texto
  const criarInputTexto = (id, label, placeholder, obrigatorio = true) => {
    return `
                    <div style="margin-bottom:10px;">
                        <label for="${id}" style="display:block; margin-bottom:3px;">${label}${
      obrigatorio ? "*" : ""
    }:</label>
                        <input type="text" id="${id}" placeholder="${placeholder}" ${
      obrigatorio ? "required" : ""
    } style="width: calc(100% - 22px);">
                    </div>
                `;
  };

  switch (tipoConsulta) {
    case "vendasPorProduto":
      containerFiltros.innerHTML += `
                <div style="margin-bottom:10px;">
                    <label for="consultaAvancadaProdutoId" style="display:block; margin-bottom:3px;">Produto Específico (Opcional):</label>
                    <select id="consultaAvancadaProdutoId" style="width:100%; padding:8px;">
                        <option value="">-- Todos os Produtos --</option>
                    </select>
                </div>
            `;
      containerFiltros.innerHTML += criarInputData(
        "consultaAvancadaDataInicial",
        "Data Inicial"
      );
      containerFiltros.innerHTML += criarInputData(
        "consultaAvancadaDataFinal",
        "Data Final"
      );
      popularSelectProdutos("consultaAvancadaProdutoId"); // Chama a função para popular
      break;
    case "volumeVendas":
    case "vendasPorProduto":
    case "taxaConversao":
      containerFiltros.innerHTML += criarInputData(
        "consultaAvancadaDataInicial",
        "Data Inicial"
      );
      containerFiltros.innerHTML += criarInputData(
        "consultaAvancadaDataFinal",
        "Data Final"
      );
      break;
    case "perfilCliente":
      // Substitui o criarInputTexto por um select
      containerFiltros.innerHTML += `
                    <div style="margin-bottom:10px;">
                        <label for="consultaAvancadaNomeClienteSelect" style="display:block; margin-bottom:3px;">Nome do Cliente*:</label>
                        <select id="consultaAvancadaNomeClienteSelect" required style="width:100%; padding:8px;">
                            <option value="">-- Carregando Clientes... --</option>
                        </select>
                    </div>
                `;
      containerFiltros.innerHTML += criarInputData(
        "consultaAvancadaDataInicial",
        "Data Inicial (Opcional)",
        false
      );
      containerFiltros.innerHTML += criarInputData(
        "consultaAvancadaDataFinal",
        "Data Final (Opcional)",
        false
      );
      popularSelectNomesClientes("consultaAvancadaNomeClienteSelect"); // Chama a função para popular
      break;
    case "baixoEstoque":
      // NENHUM FILTRO NECESSÁRIO PARA ESTE RELATÓRIO
      containerFiltros.innerHTML
      break;
    default:
  }
}

async function executarConsultaGerencialAvancada() {
  const tipoConsulta = document.getElementById(
    "tipoConsultaGerencialAvancada"
  ).value;
  const resultDivId = "resultadoConsultaAvancada";
  exibirResultadoOperacao(resultDivId, "", "normal"); // Limpa resultado anterior e esconde

  if (!tipoConsulta) {
    exibirResultadoOperacao(
      resultDivId,
      "Por favor, selecione um tipo de consulta.",
      "error"
    );
    return;
  }

  let dataInicial, dataFinal, nomeCliente, idProdutoSelecionado;
  let url;
  let queryParams = new URLSearchParams();

  // Coleta os dados dos inputs dinâmicos
  const elDataInicial = document.getElementById("consultaAvancadaDataInicial");
  const elDataFinal = document.getElementById("consultaAvancadaDataFinal");
  const elNomeClienteSelect = document.getElementById(
    "consultaAvancadaNomeClienteSelect"
  );
  const elProdutoId = document.getElementById("consultaAvancadaProdutoId");

  if (elDataInicial) dataInicial = elDataInicial.value;
  if (elDataFinal) dataFinal = elDataFinal.value;
  if (elNomeClienteSelect) nomeCliente = elNomeClienteSelect.value;
  if (elProdutoId) idProdutoSelecionado = elProdutoId.value;
  // Validações e construção da URL
  switch (tipoConsulta) {
    case "vendasPorProduto":
      url = "/gerencial/vendasPorProduto";
      if (!dataInicial || !dataFinal) {
        exibirResultadoOperacao(
          resultDivId,
          "Data inicial e final são obrigatórias.",
          "error"
        );
        return;
      }
      queryParams.append("dataInicial", dataInicial);
      queryParams.append("dataFinal", dataFinal);
      if (idProdutoSelecionado) {
        queryParams.append("idProduto", idProdutoSelecionado);
      }
      break;

    case "volumeVendas":
      url += "volumeVendas";
      if (!dataInicial || !dataFinal) {
        exibirResultadoOperacao(
          resultDivId,
          "Data inicial e final são obrigatórias.",
          "error"
        );
        return;
      }
      queryParams.append("dataInicial", dataInicial);
      queryParams.append("dataFinal", dataFinal);
      break;
    case "vendasPorProduto":
      url += "vendasPorProduto";
      if (!dataInicial || !dataFinal) {
        exibirResultadoOperacao(
          resultDivId,
          "Data inicial e final são obrigatórias.",
          "error"
        );
        return;
      }
      queryParams.append("dataInicial", dataInicial);
      queryParams.append("dataFinal", dataFinal);
      break;
    case "perfilCliente":
      url += "perfilCliente";
      if (!nomeCliente) {
        // Verifica se um cliente foi selecionado
        exibirResultadoOperacao(
          resultDivId,
          "Nome do cliente é obrigatório para esta consulta.",
          "error"
        );
        return;
      }
      queryParams.append("nomeCliente", nomeCliente);
      if (dataInicial) queryParams.append("dataInicial", dataInicial);
      if (dataFinal) queryParams.append("dataFinal", dataFinal);
      break;
    case "taxaConversao":
      url += "taxaConversao";
      if (!dataInicial || !dataFinal) {
        exibirResultadoOperacao(
          resultDivId,
          "Data inicial e final são obrigatórias.",
          "error"
        );
        return;
      }
      queryParams.append("dataInicial", dataInicial);
      queryParams.append("dataFinal", dataFinal);
      break;
    case "baixoEstoque":
      url = "/gerencial/relatorioEstoqueBaixo";
      // Não há parâmetros para adicionar
      break;
    default:
      exibirResultadoOperacao(
        resultDivId,
        "Tipo de consulta inválido.",
        "error"
      );
      return;
  }

  // Validação de período para datas, se ambas existirem
  if (dataInicial && dataFinal && new Date(dataInicial) > new Date(dataFinal)) {
    exibirResultadoOperacao(
      resultDivId,
      "A data inicial não pode ser posterior à data final.",
      "error"
    );
    return;
  }

  const fullUrl = queryParams.toString()
    ? `${url}?${queryParams.toString()}`
    : url;
  exibirResultadoOperacao(
    resultDivId,
    `Consultando ${
      document.getElementById("tipoConsultaGerencialAvancada")
        .selectedOptions[0].text
    }...`,
    "loading"
  );

  try {
    const response = await fetch(fullUrl);

    // Tratar erro primeiro (comum a todos os tipos de resposta)
    if (!response.ok) {
      let errorMsg = `Erro ${response.status}: ${response.statusText}`;
      try {
        const errorData = await response.json();
        errorMsg = `Erro ${response.status}: ${
          errorData.message || response.statusText
        }`;
      } catch (e) {
        const textError = await response.text();
        errorMsg = `Erro ${response.status}: ${
          textError || response.statusText
        }`;
      }
      throw new Error(errorMsg);
    }

    // Tratar a resposta baseando-se no tipo de consulta
    if (tipoConsulta === "baixoEstoque") {
      const dadosTexto = await response.text();
      exibirResultadoOperacao(resultDivId, dadosTexto, "normal_pre");
    } else {
      // Lógica para as outras consultas que retornam JSON
      if (response.status === 204) {
        exibirResultadoOperacao(
          resultDivId,
          "Nenhum resultado encontrado.",
          "normal"
        );
        return;
      }
      const dadosJson = await response.json();

      if (
        dadosJson === null ||
        (Array.isArray(dadosJson) && dadosJson.length === 0) ||
        (typeof dadosJson === "object" &&
          Object.keys(dadosJson).length === 0 &&
          !Array.isArray(dadosJson))
      ) {
        exibirResultadoOperacao(
          resultDivId,
          "Nenhum resultado encontrado para os filtros aplicados.",
          "normal"
        );
      } else {
        if (tipoConsulta === "vendasPorProduto" && Array.isArray(dadosJson)) {
          exibirResultadoOperacao(
            resultDivId,
            formatarVendasPorProduto(dadosJson),
            "normal_pre"
          );
        } else if (
          tipoConsulta === "perfilCliente" &&
          typeof dadosJson === "object" &&
          dadosJson.nomeCliente
        ) {
          exibirResultadoOperacao(
            resultDivId,
            formatarPerfilCliente(dadosJson),
            "normal_pre"
          );
        } else {
          exibirResultadoOperacao(resultDivId, dadosJson);
        }
      }
    }
  } catch (error) {
    exibirResultadoOperacao(
      resultDivId,
      `Erro ao executar consulta: ${error.message}`,
      "error"
    );
  }
}

// Adicionar funções de formatação específicas para os DTOs gerenciais
function formatarVolumeVendas(dados) {
  let texto = "Volume Total de Vendas:\n";
  texto += `Período: ${dados.dataInicial} a ${dados.dataFinal}\n`;
  texto += `Valor Total Vendido: R$ ${parseFloat(
    dados.valorTotalVendas
  ).toFixed(2)}\n`;
  texto += `Orçamentos Considerados: ${dados.quantidadeOrcamentosConsiderados}\n`;
  return texto;
}

function formatarTaxaConversao(dados) {
  let texto = "Taxa de Conversão de Orçamentos:\n";
  texto += `Período: ${dados.dataInicialPeriodo} a ${dados.dataFinalPeriodo}\n`;
  texto += `Total de Orçamentos Criados: ${dados.totalOrcamentosCriados}\n`;
  texto += `Total de Orçamentos Efetivados: ${dados.totalOrcamentosEfetivados}\n`;
  texto += `Percentual de Conversão: ${parseFloat(
    dados.percentualConversao
  ).toFixed(2)}%\n`;
  return texto;
}

function formatarVendasPorProduto(dados) {
  if (!dados || dados.length === 0)
    return "Nenhum dado de vendas por produto encontrado.";
  let texto = "Vendas por Produto:\n";
  texto +=
    "ID Produto | Descrição                               | Qtd. Vendida | Valor Arrecadado (R$)\n";
  texto +=
    "-------------------------------------------------------------------------------------------------\n";
  dados.forEach((p) => {
    texto += `${String(p.idProduto).padEnd(10)} | ${String(p.descricaoProduto)
      .padEnd(40)
      .substring(0, 40)} | ${String(p.quantidadeTotalVendida).padStart(
      12
    )} | ${parseFloat(p.valorTotalArrecadado).toFixed(2).padStart(20)}\n`;
  });
  return texto;
}

function formatarPerfilCliente(dados) {
  if (!dados) return "Nenhum dado de perfil de cliente encontrado.";
  let texto = `Perfil do Cliente: ${dados.nomeCliente}\n`;
  texto += `Email do Cliente: ${dados.emailCliente}\n`;
  texto += `CPF: ${dados.cpfCliente}\n`;
  texto += "----------------------------------------------------------\n";

  if (dados.dataInicialFiltro || dados.dataFinalFiltro) {
    texto += `Período do Filtro: ${dados.dataInicialFiltro || "N/A"} a ${
      dados.dataFinalFiltro || "N/A"
    }\n`;
  }

  texto += `Total Gasto: R$ ${parseFloat(dados.totalGastoPeloCliente).toFixed(
    2
  )}\n`;
  texto += `Total de Orçamentos Efetivados Considerados: ${dados.totalOrcamentosEfetivadosConsiderados}\n\n`;

  texto += "----------------------------------------------------------\n";

  texto += "Itens Comprados:\n";
  texto +=
    "ID Produto | Descrição                               | Qtd. Comprada | Valor Gasto (R$)\n";
  texto +=
    "-------------------------------------------------------------------------------------------------\n";
  if (dados.itensComprados && dados.itensComprados.length > 0) {
    dados.itensComprados.forEach((item) => {
      texto += `${String(item.idProduto).padEnd(10)} | ${String(
        item.descricaoProduto
      )
        .padEnd(40)
        .substring(0, 40)} | ${String(item.quantidadeTotalComprada).padStart(
        13
      )} | ${parseFloat(item.valorTotalGastoNoProduto)
        .toFixed(2)
        .padStart(17)}\n`;
    });
  } else {
    texto +=
      "Nenhum item comprado encontrado para este cliente (ou nos filtros aplicados).\n";
  }
  return texto;
}

let produtosCarregadosParaGerenciamento = [];
let produtosDisponiveisParaDropdown = [];
let orcamentosCarregados = [];
let produtoFormWindow = null;

async function handleFetchError(response) {
  if (!response.ok) {
    let errorMsg = `Erro ${response.status}: ${response.statusText}`;
    try {
      const errorData = await response.json();
      errorMsg = `Erro ${response.status}: ${
        errorData.message || response.statusText
      }`;
    } catch (e) {
    }
    throw new Error(errorMsg);
  }
  if (response.status === 204) return null;
  return response.json();
}

window.onload = async function () {
  await carregarProdutosParaGerenciamento();
  await carregarTodosOrcamentosParaGerenciamento();
  await carregarProdutosParaDropdownsOrcamento();
  adicionarLinhaDeItemParaOrcamento();
};

async function carregarProdutosParaDropdownsOrcamento() {
  try {
    const response = await fetch("/produtosDisponiveis");
    produtosDisponiveisParaDropdown = await handleFetchError(response);
    atualizarTodosOsSeletoresDeProduto();
  } catch (error) {
    console.error("Erro ao carregar produtos para dropdowns:", error);
  }
}

function abrirFormularioProdutoPopup(produtoId = null) {
  let url = "produto_form.html";
  if (produtoId) {
    url += "?id=" + produtoId;
  }
  if (produtoFormWindow && !produtoFormWindow.closed) {
    produtoFormWindow.close();
  }
  produtoFormWindow = window.open(
    url,
    "ProdutoForm",
    "width=600,height=750,scrollbars=yes,resizable=yes,status=yes"
  );
  if (produtoFormWindow) {
    produtoFormWindow.focus();
  } else {
    alert("Por favor, permita pop-ups para este site.");
  }
}

function notificarProdutoSalvo() {
  console.log("Popup notificou que produto foi salvo. Recarregando listas...");
  if (produtoFormWindow && !produtoFormWindow.closed) {
    produtoFormWindow.close();
  }
  exibirResultadoOperacao(
    "produtoFormGlobalResult",
    "Produto salvo com sucesso através do formulário! Atualizando lista...",
    "normal"
  );
  setTimeout(() => {
    document.getElementById("produtoFormGlobalResult").style.display = "none";
  }, 4000);
  carregarProdutosParaGerenciamento();
  carregarProdutosParaDropdownsOrcamento();
}

async function carregarProdutosParaGerenciamento() {
  try {
    // Limpa os resultados anteriores antes de carregar.
    document.getElementById("produtosGerenciamentoResult").style.display =
      "none";
    document.getElementById("produtosGerenciamentoResult").innerHTML = "";

    const response = await fetch("/todosProdutosStatus");
    produtosCarregadosParaGerenciamento = await handleFetchError(response);
    renderizarProdutosComoCards();
    if (
      document
        .getElementById("produtosGerenciamentoResult")
        .classList.contains("Carregando")
    ) {
      document.getElementById("produtosGerenciamentoResult").style.display =
        "none";
    }
  } catch (error) {
    console.error("Erro ao carregar produtos para gerenciamento:", error);
    exibirResultadoOperacao(
      "produtosGerenciamentoResult",
      "Falha ao carregar produtos para gerenciamento: " + error.message,
      "error"
    );
  }
}

function renderizarProdutosComoCards() {
  const container = document.getElementById("produtosGerenciamentoContainer");
  container.innerHTML = "";
  const mostrarDelistados = document.getElementById(
    "chkMostrarDelistados"
  ).checked;

  const produtosParaRenderizar = produtosCarregadosParaGerenciamento.filter(
    (produto) => {
      return mostrarDelistados || produto.listado;
    }
  );

  if (!produtosParaRenderizar || produtosParaRenderizar.length === 0) {
    container.innerHTML =
      "<p>Nenhum produto encontrado para os critérios selecionados.</p>";
    return;
  }

  produtosParaRenderizar.forEach((produto) => {
    const cardDiv = document.createElement("div");
    cardDiv.className = "product-card";
    let statusInfo = "";
    let actionButtons = "";

    if (!produto.listado) {
      cardDiv.style.opacity = "0.6";
      statusInfo = '<p style="color: #777; font-weight: bold;">DE-LISTADO</p>';
      actionButtons = `<button onclick="alert('Funcionalidade Relistar Produto (ID: ${produto.id}) a ser implementada.')">Relistar</button>`;
    } else {
      if (produto.quantidadeEmEstoque === 0) {
        statusInfo =
          '<p style="color: orange; font-weight: bold;">SEM ESTOQUE</p>';
      } else if (
        produto.quantidadeEmEstoque > 0 &&
        produto.quantidadeEmEstoque < produto.estoqueMin
      ) {
        statusInfo = `<p style="color: darkorange; font-weight: bold;">BAIXO ESTOQUE (${produto.quantidadeEmEstoque} de ${produto.estoqueMin} min)</p>`;
      } else {
        // quantidadeEmEstoque >= estoqueMin
        statusInfo = `<p style="color: green; font-weight: bold;">EM ESTOQUE (${produto.quantidadeEmEstoque})</p>`;
      }
      actionButtons = `
                        <button onclick="abrirFormularioProdutoPopup(${
                          produto.id
                        })">Editar</button>
                        <button class="remove" onclick="desativarProdutoPeloCard(${
                          produto.id
                        }, '${produto.descricao.replace(
        /'/g,
        "\\'"
      )}')">De-listar</button>
                    `;
    }

    cardDiv.innerHTML = `
                    <div>
                        <p class="product-id">ID: ${produto.id}</p>
                        <p><strong>${produto.descricao}</strong></p>
                        <p class="product-price">R$ ${(
                          produto.precoUnitario || 0
                        ).toFixed(2)}</p>
                        ${statusInfo}
                        <p style="font-size:0.8em">Min:${
                          produto.estoqueMin
                        } / Max:${produto.estoqueMax}</p>
                    </div>
                    <div class="button-group" style="margin-top: auto;">
                        ${actionButtons}
                    </div>
                `;
    container.appendChild(cardDiv);
  });
}

async function carregarTodosOrcamentosParaGerenciamento() {
  try {
    const response = await fetch("/todosOrcamentos");
    orcamentosCarregados = await handleFetchError(response);
    renderizarListaDeOrcamentos();
  } catch (error) {
    console.error("Erro ao carregar orçamentos:", error);
    exibirResultadoOperacao(
      "gerenciarOrcamentoResult",
      "Falha ao carregar lista de orçamentos: " + error.message,
      "error"
    );
  }
}

function atualizarTodosOsSeletoresDeProduto() {
  const seletores = document.querySelectorAll("select.prodId");
  seletores.forEach((seletor) => {
    const valorAtual = seletor.value;
    seletor.innerHTML = '<option value="">Selecione um produto...</option>';
    if (produtosDisponiveisParaDropdown) {
      produtosDisponiveisParaDropdown.forEach((produto) => {
        const option = document.createElement("option");
        option.value = produto.id;
        // Assumindo que produtosDisponiveisParaDropdown contém items com id, descricao, precoUnitario
        option.textContent = `${produto.id} - ${produto.descricao} (R$ ${(
          produto.precoUnitario || 0
        ).toFixed(2)})`;
        seletor.appendChild(option);
      });
    }
    if (valorAtual) seletor.value = valorAtual;
  });
}

function renderizarListaDeOrcamentos() {
  const divLista = document.getElementById("listaOrcamentosParaGerenciar");
  divLista.innerHTML = "";

  if (!orcamentosCarregados || orcamentosCarregados.length === 0) {
    divLista.innerHTML = "<p>Nenhum orçamento encontrado para gerenciar.</p>";
    return;
  }
  const ul = document.createElement("ul");
  orcamentosCarregados.sort((a, b) => a.id - b.id);
  orcamentosCarregados.forEach((orc) => {
    const li = document.createElement("li");
    const status = orc.efetivado
      ? '<span style="color: green;">(EFETIVADO)</span>'
      : '<span style="color: orange;">(PENDENTE)</span>';
    let clienteInfo = orc.estadoCliente
      ? ` [${orc.estadoCliente.toUpperCase()}]`
      : "";
    let botoesAcao = '<div class="orc-actions">';
    if (!orc.efetivado) {
      botoesAcao += `<button onclick="efetivarOrcamentoDaLista(${orc.id})">Efetivar</button>`;
    }
    botoesAcao += `<button class="remove" onclick="removerOrcamentoDaLista(${orc.id})">Remover</button>`;
    botoesAcao += "</div>";

    li.innerHTML = `<span class="orc-info"><strong>Orçamento #${
      orc.id
    }</strong>${clienteInfo} - Total: R$ ${(orc.custoConsumidor || 0).toFixed(
      2
    )} ${status}</span> ${botoesAcao}`;
    ul.appendChild(li);
  });
  divLista.appendChild(ul);
}

function formatarComoJson(data) {
  return JSON.stringify(data, null, 2);
}

function exibirResultadoOperacao(elementId, data, tipo = "normal") {
  const el = document.getElementById(elementId);
  if (!el) {
    console.error("Elemento de resultado não encontrado:", elementId);
    return;
  }
  el.style.display = "block";
  el.classList.remove("loading", "error");

  if (tipo === "loading") {
    el.classList.add("loading");
    el.innerHTML = typeof data === "string" ? data : "Processando...";
  } else if (tipo === "error") {
    el.classList.add("error");
    el.innerHTML = typeof data === "string" ? data : "Ocorreu um erro.";
  } else {
    el.innerHTML =
      typeof data === "object" && data !== null
        ? formatarComoJson(data)
        : data === null
        ? "Operação concluída."
        : data;
  }
}

async function exibirProdutosGerenciamentoComoJson() {
  if (
    produtosCarregadosParaGerenciamento &&
    produtosCarregadosParaGerenciamento.length > 0
  ) {
    exibirResultadoOperacao(
      "produtosGerenciamentoResult",
      produtosCarregadosParaGerenciamento
    );
  } else {
    // Tenta carregar caso esteja vazio.
    await carregarProdutosParaGerenciamento();
    if (
      produtosCarregadosParaGerenciamento &&
      produtosCarregadosParaGerenciamento.length > 0
    ) {
      exibirResultadoOperacao(
        "produtosGerenciamentoResult",
        produtosCarregadosParaGerenciamento
      );
    } else {
      exibirResultadoOperacao(
        "produtosGerenciamentoResult",
        "Nenhum produto carregado para exibir como JSON.",
        "error"
      );
    }
  }
}

function adicionarLinhaDeItemParaOrcamento() {
  const container = document.getElementById("itensOrcamento");
  const novaLinha = document.createElement("div");
  novaLinha.className = "item";
  novaLinha.innerHTML = `
                <select class="prodId"><option value="">Selecione...</option></select>
                <input type="number" placeholder="Qtd" class="prodQtd" min="1" value="1">
                <button class="remove" onclick="this.parentElement.remove()">Remover</button>
            `;
  container.appendChild(novaLinha);
  atualizarTodosOsSeletoresDeProduto();
}

async function submeterNovoOrcamento() {
  const itens = [];
  document.querySelectorAll("#itensOrcamento .item").forEach((div) => {
    const sel = div.querySelector(".prodId");
    const qtd = div.querySelector(".prodQtd");
    if (sel.value && qtd.value && parseInt(qtd.value) > 0) {
      itens.push({
        idProduto: parseInt(sel.value),
        qtdade: parseInt(qtd.value),
      });
    }
  });

  const nomeCliente = document.getElementById("nomeCliente").value.trim();
  const cpfCliente = document.getElementById("cpfCliente").value.trim(); // Novo campo
  const emailCliente = document.getElementById("emailCliente").value.trim(); // Novo campo
  const paisCliente = document.getElementById("paisCliente").value.trim();
  const estadoCliente = document.getElementById("estadoCliente").value.trim();

  // Validações básicas no frontend (podem ser mais elaboradas)
  if (!nomeCliente) {
    exibirResultadoOperacao(
      "orcamentoResult",
      "Por favor, informe o nome do cliente.",
      "error"
    );
    return;
  }
  // CPF e Email podem ser opcionais dependendo da sua regra de negócio para criar/encontrar cliente.
  // Se o CPF for informado, você pode adicionar uma validação de formato básica.
  // if (cpfCliente && !validarCPF(cpfCliente)) { // Função validarCPF precisaria ser implementada
  //     exibirResultadoOperacao('orcamentoResult', 'CPF inválido.', 'error'); return;
  // }
  // if (emailCliente && !validarEmail(emailCliente)) { // Função validarEmail precisaria ser implementada
  //     exibirResultadoOperacao('orcamentoResult', 'E-mail inválido.', 'error'); return;
  // }
  if (!paisCliente) {
    exibirResultadoOperacao(
      "orcamentoResult",
      "Por favor, informe o país do cliente.",
      "error"
    );
    return;
  }
  if (!estadoCliente) {
    exibirResultadoOperacao(
      "orcamentoResult",
      "Por favor, informe o estado do cliente (Ex: RS).",
      "error"
    );
    return;
  }
  if (itens.length === 0) {
    exibirResultadoOperacao(
      "orcamentoResult",
      "Adicione itens válidos ao orçamento.",
      "error"
    );
    return;
  }

  const payload = {
    itens: itens,
    nomeCliente: nomeCliente,
    cpfCliente: cpfCliente,
    emailCliente: emailCliente, 
    paisCliente: paisCliente,
    estadoCliente: estadoCliente,
  };

  exibirResultadoOperacao("orcamentoResult", "Criando orçamento...", "loading");
  try {
    const response = await fetch("/novoOrcamento", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    const orcCriado = await handleFetchError(response);

    let displayText = "Orçamento Criado com Sucesso:\n";
    displayText += `ID: ${orcCriado.id}\n`;
    displayText += `Cliente: ${orcCriado.nomeCliente}\n`;
    displayText += `Local: ${orcCriado.paisCliente} / ${orcCriado.estadoCliente}\n`;
    displayText += `Data Geração: ${orcCriado.dataGeracao}\n`;
    displayText += `SubTotal: R$ ${parseFloat(orcCriado.subTotal).toFixed(
      2
    )}\n`;
    displayText += `Imposto Estadual: ${orcCriado.impostoEstadualFormatado}\n`;
    displayText += `Imposto Federal: ${orcCriado.impostoFederalFormatado}\n`;
    displayText += `Desconto Total: R$ ${parseFloat(
      orcCriado.valorDescontoTotal
    ).toFixed(2)}\n`;
    displayText += `Custo Consumidor: R$ ${parseFloat(
      orcCriado.custoConsumidor
    ).toFixed(2)}\n`;
    displayText += `Efetivado: ${orcCriado.efetivado}\n`;

    exibirResultadoOperacao("orcamentoResult", displayText, "normal_pre");

    // Limpar campos
    document.getElementById("nomeCliente").value = "";
    document.getElementById("cpfCliente").value = "";
    document.getElementById("emailCliente").value = "";
    document.getElementById("paisCliente").value = "Brasil";
    document.getElementById("estadoCliente").value = "";
    document.getElementById("itensOrcamento").innerHTML = "";
    adicionarLinhaDeItemParaOrcamento();
    await carregarTodosOrcamentosParaGerenciamento();
  } catch (error) {
    exibirResultadoOperacao(
      "orcamentoResult",
      "Erro ao criar orçamento: " + error.message,
      "error"
    );
  }
}

// Modificar exibirResultadoOperacao para aceitar um tipo 'normal_pre'
function exibirResultadoOperacao(elementId, data, tipo = "normal") {
  const el = document.getElementById(elementId);
  if (!el) {
    console.error("Elemento de resultado não encontrado:", elementId);
    return;
  }
  el.style.display = "block";
  el.className = "result"; // Reset classes
  el.classList.remove("loading", "error"); // Remove classes específicas

  if (tipo === "loading") {
    el.classList.add("loading");
    el.innerHTML = typeof data === "string" ? data : "Processando...";
  } else if (tipo === "error") {
    el.classList.add("error");
    el.innerHTML = typeof data === "string" ? data : "Ocorreu um erro.";
  } else if (tipo === "normal_pre") {
    // Novo tipo para usar <pre>
    el.innerHTML = `<pre>${
      typeof data === "object" && data !== null
        ? formatarComoJson(data)
        : data === null
        ? "Operação concluída."
        : data
    }</pre>`;
  } else {
    // tipo 'normal'
    el.innerHTML =
      typeof data === "object" && data !== null
        ? formatarComoJson(data)
        : data === null
        ? "Operação concluída."
        : data;
  }
}

async function efetivarOrcamentoDaLista(orcamentoId) {
  if (!orcamentoId) {
    exibirResultadoOperacao(
      "gerenciarOrcamentoResult",
      "ID inválido.",
      "error"
    );
    return;
  }
  exibirResultadoOperacao(
    "gerenciarOrcamentoResult",
    `Efetivando orçamento #${orcamentoId}...`,
    "loading"
  );
  try {
    const response = await fetch(`/efetivaOrcamento/${orcamentoId}`);
    const orcProcessado = await handleFetchError(response);
    // Verifica se o orçamento realmente foi efetivado (Talvez haja um problema)
    if (orcProcessado && orcProcessado.efetivado) {
      exibirResultadoOperacao("gerenciarOrcamentoResult", orcProcessado);
    } else if (orcProcessado) {
      exibirResultadoOperacao(
        "gerenciarOrcamentoResult",
        {
          ...orcProcessado,
          mensagem:
            "Orçamento não efetivado devido à falta de estoque ou outro problema.",
        },
        "error"
      );
    } else {
      // Não deve acontecer se o ID for inválido.
      exibirResultadoOperacao(
        "gerenciarOrcamentoResult",
        "Falha ao processar efetivação. Orçamento não encontrado ou resposta inválida.",
        "error"
      );
    }
    await carregarTodosOrcamentosParaGerenciamento();
    await carregarProdutosParaGerenciamento(); // Atualiza os produtos se houver uma mudança.
    await carregarProdutosParaDropdownsOrcamento(); // Atualiza o dropdown se houver uma mudança.
  } catch (error) {
    exibirResultadoOperacao(
      "gerenciarOrcamentoResult",
      `Erro ao efetivar orçamento #${orcamentoId}: ${error.message}`,
      "error"
    );
  }
}

async function removerOrcamentoDaLista(orcamentoId) {
  if (!orcamentoId) {
    exibirResultadoOperacao(
      "gerenciarOrcamentoResult",
      "ID inválido.",
      "error"
    );
    return;
  }
  if (
    !confirm(
      `Remover orçamento #${orcamentoId}? Esta ação não pode ser desfeita.`
    )
  )
    return;

  exibirResultadoOperacao(
    "gerenciarOrcamentoResult",
    `Removendo orçamento #${orcamentoId}...`,
    "loading"
  );
  try {
    const response = await fetch(`/orcamentos/${orcamentoId}`, {
      method: "DELETE",
    });
    await handleFetchError(response);
    exibirResultadoOperacao(
      "gerenciarOrcamentoResult",
      `Orçamento #${orcamentoId} removido.`,
      "normal"
    );
    await carregarTodosOrcamentosParaGerenciamento();
  } catch (error) {
    exibirResultadoOperacao(
      "gerenciarOrcamentoResult",
      `Erro ao remover orçamento #${orcamentoId}: ${error.message}`,
      "error"
    );
  }
}

async function desativarProdutoPeloCard(produtoId, produtoDescricao) {
  if (!produtoId) {
    exibirResultadoOperacao(
      "produtosGerenciamentoResult",
      "ID do produto inválido.",
      "error"
    );
    return;
  }
  if (
    !confirm(
      `De-listar o produto "${produtoDescricao}" (ID: ${produtoId})?\nIsso o tornará indisponível para novos orçamentos.\nOrçamentos pendentes que contêm este produto podem ser afetados.`
    )
  )
    return;

  exibirResultadoOperacao(
    "produtosGerenciamentoResult",
    `De-listando produto ID ${produtoId}...`,
    "loading"
  );
  try {
    const response = await fetch(`/produtos/${produtoId}`, {
      method: "DELETE",
    });
    await handleFetchError(response);
    exibirResultadoOperacao(
      "produtosGerenciamentoResult",
      `Produto ID ${produtoId} de-listado. Orçamentos podem ter sido atualizados.`,
      "normal"
    );
    await carregarProdutosParaGerenciamento();
    await carregarTodosOrcamentosParaGerenciamento();
    await carregarProdutosParaDropdownsOrcamento();
  } catch (error) {
    exibirResultadoOperacao(
      "produtosGerenciamentoResult",
      `Erro ao de-listar produto ID ${produtoId}: ${error.message}`,
      "error"
    );
  }
}

async function buscarOrcamentosEfetivadosPorPeriodoDefinido(periodo) {
  let dataFinal = new Date(); // Data de hoje como base para o fim do período
  let dataInicial = new Date();
  let tituloPeriodo = "";

  switch (periodo) {
    case "dia":
      // dataInicial e dataFinal já são 'hoje'
      tituloPeriodo = "hoje";
      break;
    case "semana":
      dataInicial.setDate(dataFinal.getDate() - 6); // Últimos 7 dias (inclusive hoje)
      tituloPeriodo = "nos últimos 7 dias";
      break;
    case "quinzena":
      dataInicial.setDate(dataFinal.getDate() - 14); // Últimos 15 dias (inclusive hoje)
      tituloPeriodo = "nos últimos 15 dias";
      break;
    case "mes":
      dataInicial.setDate(dataFinal.getDate() - 29); // Últimos 30 dias (inclusive hoje)
      tituloPeriodo = "nos últimos 30 dias";
      break;
    default:
      exibirResultadoOperacao(
        "ultimosResult",
        "Período inválido selecionado.",
        "error"
      );
      return;
  }

  // Formatar as datas para o formato AAAA-MM-DD exigido pelo backend
  const dataFinalStr = dataFinal.toISOString().split("T")[0];
  const dataInicialStr = dataInicial.toISOString().split("T")[0];

  exibirResultadoOperacao(
    "ultimosResult",
    `Buscando orçamentos efetivados ${tituloPeriodo} (de ${dataInicialStr} a ${dataFinalStr})...`,
    "loading"
  );
  try {
    const response = await fetch(
      `/orcamentosEfetivados?dataInicial=${dataInicialStr}&dataFinal=${dataFinalStr}`
    );
    const dados = await handleFetchError(response); // Sua função de tratamento de erro
    if (dados && dados.length > 0) {
      exibirResultadoOperacao("ultimosResult", dados);
    } else {
      exibirResultadoOperacao(
        "ultimosResult",
        `Nenhum orçamento efetivado encontrado ${tituloPeriodo}.`,
        "normal"
      );
    }
  } catch (error) {
    exibirResultadoOperacao(
      "ultimosResult",
      "Erro ao buscar relatório: " + error.message,
      "error"
    );
  }
}
