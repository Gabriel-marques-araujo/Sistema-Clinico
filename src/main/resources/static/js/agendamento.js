function adicionarCSSInline(estilos) {
    var style = document.createElement("style");
    style.type = "text/css";
    style.appendChild(document.createTextNode(estilos));
    document.head.appendChild(style);
}

// Chamar a função com os estilos CSS
adicionarCSSInline(`
    .rating-section {
        margin-bottom: 30px;
        text-align: center;
    }
    .rating {
        display: flex;
        justify-content: center;
        flex-direction: row-reverse;
        margin-bottom: 20px;
    }
    .rating input {
        display: none;
    }
    .rating label {
        font-size: 2em;
        color: #ddd;
        cursor: pointer;
        transition: transform 0.2s ease, color 0.2s ease;
    }
    .rating label:hover,
    .rating label:hover ~ label {
        transform: scale(1.2);
        color: #ffcc00;
    }
    .rating input:checked ~ label {
        color: #ffcc00;
    }
    .rating label:hover ~ label {
        color: #ddd;
    }
    .selected-rating {
        margin-top: 10px;
        font-size: 1.05em;
    }
    .comment-section {
        display: flex;
        justify-content: center;
        margin-top: 20px;
    }
    .comment-section label {
        margin-right: 10px;
        font-size: 1.05em;
    }
    .comment-section input {
        width: 300px;
        padding: 10px;
        font-size: 1.05em;
    }
`);

function loadSweetAlert(callback) {
    var script = document.createElement("script");
    script.src = "https://cdn.jsdelivr.net/npm/sweetalert2@11";
    script.onload = callback;
    document.body.appendChild(script);

    var link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = "https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css";
    document.head.appendChild(link);
}

function abrirAvaliacao(agendamentoId) {
    let urlAtual = window.location.href;

    if (urlAtual === "http://localhost:8080/agendamentos/historico/consultas") {
        if (typeof Swal === "undefined") {
            loadSweetAlert(() => showAlert(agendamentoId));
        } else {
            showAlert(agendamentoId);
        }
    } else if (urlAtual === "http://localhost:8080/agendamentos/historico/paciente") {
        if (typeof Swal === "undefined") {
            loadSweetAlert(() => showAvaliacaoAlert(agendamentoId));
        } else {
            showAvaliacaoAlert(agendamentoId);
        }
    }
}

function showAvaliacaoAlert(agendamentoId) {
    fetch(`/agendamentos/${agendamentoId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Erro ao buscar dados do agendamento");
            return response.json();
        })
        .then(data => {
            // Verifica se a consulta foi realizada (se descricao está preenchida)
            if (!data.descricao || data.descricao.trim() === "") {
                Swal.fire({
                    title: "Atenção",
                    text: "A consulta precisa ser realizada antes de ser avaliada. Por favor, registre o prontuário primeiro.",
                    icon: "warning",
                    confirmButtonText: "OK"
                });
                return;
            }

            // Se a consulta foi realizada, exibe o modal de avaliação
            Swal.fire({
                title: "Avaliação",
                html: `
                <label>Médico:</label>
                <input type="text" id="nomeMedico" class="swal2-input" readonly value="${data.medico.nome}"><br>
                <label>Especialidade:</label>
                <input type="text" id="especialidade" class="swal2-input" readonly value="${data.especialidade.titulo}"><br>
                <label>Selecione a quantidade de estrelas</label>
                <div class="rating">
                    <input type="radio" id="star5" name="rating" value="5">
                    <label for="star5">★</label>
                    <input type="radio" id="star4" name="rating" value="4">
                    <label for="star4">★</label>
                    <input type="radio" id="star3" name="rating" value="3">
                    <label for="star3">★</label>
                    <input type="radio" id="star2" name="rating" value="2">
                    <label for="star2">★</label>
                    <input type="radio" id="star1" name="rating" value="1">  
                    <label for="star1">★</label>
                </div>
                <label>Comentário:</label>
                <div>
                    <textarea id="comentario" class="swal2-textarea">${data.comentarioAvaliacao || ''}</textarea><br>
                </div>
            `,
                showCancelButton: true,
                confirmButtonText: "Salvar Avaliação",
                cancelButtonText: "Cancelar",
                didOpen: () => {
                    if (data.notaAvaliacao) {
                        document.getElementById(`star${data.notaAvaliacao}`).checked = true;
                    }
                },
                preConfirm: () => {
                    let nota = document.querySelector('input[name="rating"]:checked');
                    let comentario = document.getElementById("comentario").value;

                    if (!nota) {
                        Swal.showValidationMessage("Por favor, selecione uma avaliação");
                        return false;
                    }

                    return fetch(`/agendamentos/avaliacao/${agendamentoId}`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            nota: parseInt(nota.value),
                            comentario: comentario
                        })
                    })
                        .then(response => {
                            if (!response.ok) throw new Error("Erro ao salvar avaliação");
                            return response.json();
                        })
                        .then(data => {
                            Swal.fire("Avaliação salva!", "Sua avaliação foi salva com sucesso.", "success");
                        })
                        .catch(error => {
                            Swal.fire("Erro", "Erro ao salvar avaliação: " + error.message, "error");
                        });
                }
            });
        })
        .catch(error => {
            Swal.fire("Erro", "Não foi possível carregar os dados do agendamento", "error");
        });
}

function showAlert(agendamentoId) {
    Swal.fire({
        title: "Prontuário",
        html: `
            <label>Nome do Médico:</label>
            <input type="text" id="nomeMedico" class="swal2-input" readonly><br>
            <label>Especialidade:</label>
            <input type="text" id="especialidade" class="swal2-input" readonly><br>
            <label>Descrição da Consulta:</label>
            <textarea id="descricao1" class="swal2-textarea"></textarea><br>
            <label>O paciente possui plano de saúde?</label>
            <select id="planoSaude" class="swal2-select" onchange="toggleValorConsulta()">
                <option value="Sim">Sim</option>
                <option value="Não">Não</option>
            </select><br>
            <div id="valorConsultaContainer" style="display: none;">
                <label>Valor da Consulta:</label>
                <input type="number" id="valorConsulta" class="swal2-input" min="0" step="0.01"><br>
            </div>
        `,
        showCancelButton: true,
        confirmButtonText: "Salvar Consulta",
        cancelButtonText: "Cancelar",
        didOpen: () => {
            fetch(`/agendamentos/${agendamentoId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
                .then(response => {
                    if (!response.ok) throw new Error("Erro ao buscar dados do agendamento");
                    return response.json();
                })
                .then(data => {
                    document.getElementById("nomeMedico").value = data.medico.nome;
                    document.getElementById("especialidade").value = data.especialidade.titulo;
                    if (data.descricao) document.getElementById("descricao1").value = data.descricao;
                    if (data.possuiPlanoSaude !== null) {
                        document.getElementById("planoSaude").value = data.possuiPlanoSaude ? "Sim" : "Não";
                        toggleValorConsulta();
                        if (!data.possuiPlanoSaude && data.valorConsulta) {
                            document.getElementById("valorConsulta").value = data.valorConsulta;
                        }
                    }
                })
                .catch(error => {
                    Swal.fire("Erro", "Não foi possível carregar os dados do agendamento", "error");
                });
        },
        preConfirm: () => {
            let descricao = document.getElementById("descricao1").value;
            let planoSaude = document.getElementById("planoSaude").value === "Sim";
            let valorConsulta = planoSaude ? null : parseFloat(document.getElementById("valorConsulta").value || 0);

            if (!descricao) {
                Swal.showValidationMessage("A descrição é obrigatória");
                return false;
            }

            return fetch(`/agendamentos/prontuario/${agendamentoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    descricao: descricao,
                    possuiPlanoSaude: planoSaude,
                    valorConsulta: valorConsulta
                })
            })
                .then(response => {
                    if (!response.ok) throw new Error("Erro ao salvar prontuário");
                    return response.json();
                })
                .then(data => {
                    Swal.fire("Consulta salva!", "Prontuário registrado com sucesso.", "success");
                })
                .catch(error => {
                    Swal.fire("Erro", "Erro ao salvar prontuário: " + error.message, "error");
                });
        }
    });
}

function toggleValorConsulta() {
    let planoSaude = document.getElementById("planoSaude").value;
    let valorConsultaContainer = document.getElementById("valorConsultaContainer");
    if (planoSaude === "Não") {
        valorConsultaContainer.style.display = "block";
    } else {
        valorConsultaContainer.style.display = "none";
    }
}

/**
 * busca as especialidades com auto-complete
 */
$("#especialidade").autocomplete({
    source: function (request, response) {
        $.ajax({
            method: "GET",
            url: "/especialidades/titulo",
            data: {
                termo: request.term
            },
            success: function (data) {
                response(data);
            }
        });
    }
});

/**
 * após a especialidade ser selecionado busca
 * os médicos referentes e os adiciona na página com
 * radio
 */
$('#especialidade').on('blur', function() {
    $('div').remove(".custom-radio");
    var titulo = $(this).val();
    var usuarioEmail = $(".usuario").text().trim();

    if (titulo != '') {
        $.get("/medicos/especialidade/titulo-plano/", { titulo: titulo, email: usuarioEmail }, function(result) {
            var ultimo = result.length - 1;

            $.each(result, function (k, v) {
                if (k == ultimo) {
                    $("#medicos").append(
                        '<div class="custom-control custom-radio">' +
                        '<input class="custom-control-input" type="radio" id="customRadio'+ k +'" name="medico.id" value="'+ v.id +'" required>' +
                        '<label class="custom-control-label" for="customRadio'+ k +'">'+ v.nome +'</label>' +
                        '<div class="invalid-feedback">Médico é obrigatório</div>' +
                        '</div>'
                    );
                } else {
                    $("#medicos").append(
                        '<div class="custom-control custom-radio">' +
                        '<input class="custom-control-input" type="radio" id="customRadio'+ k +'" name="medico.id" value="'+ v.id +'" required>' +
                        '<label class="custom-control-label" for="customRadio'+ k +'">'+ v.nome +'</label>' +
                        '</div>'
                    );
                }
            });
        });
    }
});

/**
 * busca os horários livres para consulta conforme a data e o médico
 */
$('#data').on('blur', function () {
    $("#horarios").empty();
    var data = $(this).val();
    var medico = $('input[name="medico.id"]:checked').val();
    if (!Date.parse(data)) {
        console.log('data nao selecionada');
    } else {
        $.get('/agendamentos/horario/medico/'+ medico + '/data/' + data , function(result) {
            $.each(result, function (k, v) {
                $("#horarios").append(
                    '<option class="op" value="'+ v.id +'">'+ v.horaMinuto + '</option>'
                );
            });
        });
    }
});

/**
 * Datatable histórico de consultas
 */
$(document).ready(function() {
    moment.locale('pt-BR');
    var table = $('#table-paciente-historico').DataTable({
        searching: false,
        lengthMenu: [5, 10],
        processing: true,
        serverSide: true,
        responsive: true,
        order: [2, 'desc'],
        ajax: {
            url: '/agendamentos/datatables/server/historico',
            data: 'data'
        },
        columns: [
            {data: 'id'},
            {data: 'paciente.nome'},
            {data: 'dataConsulta', render: function(dataConsulta) {
                    return moment(dataConsulta).format('LLL');
                }},
            {data: 'medico.nome'},
            {data: 'especialidade.titulo'},
            {orderable: false, data: 'id', render: function(id) {
                    let urlAtual = window.location.href;
                    if (urlAtual === "http://localhost:8080/agendamentos/historico/consultas") {
                        var editarHeader = document.querySelector("th[scope='col']:nth-child(6)");
                        if (editarHeader) {
                            editarHeader.textContent = "Consulta";
                        }
                        return `<a class="btn btn-success btn-sm btn-block" onclick="abrirAvaliacao(${id})" role="button"><i class="fas fa-edit"></i></a>`;
                    } else if (urlAtual === "http://localhost:8080/agendamentos/historico/paciente") {
                        var editarHeader = document.querySelector("th[scope='col']:nth-child(6)");
                        if (editarHeader) {
                            editarHeader.textContent = "Avaliar";
                        }
                        return `<a class="btn btn-success btn-sm btn-block" onclick="abrirAvaliacao(${id})" role="button"><i class="fas fa-edit"></i></a>`;
                    } else {
                        return `<a class="btn btn-success btn-sm btn-block" href="/agendamentos/editar/consulta/${id}" role="button"><i class="fas fa-edit"></i></a>`;
                    }
                }},
            {orderable: false, data: 'id', render: function(id) {
                    return `<a class="btn btn-danger btn-sm btn-block" href="/agendamentos/excluir/consulta/${id}" role="button" data-toggle="modal" data-target="#confirm-modal"><i class="fas fa-times-circle"></i></a>`;
                }}
        ]
    });
});