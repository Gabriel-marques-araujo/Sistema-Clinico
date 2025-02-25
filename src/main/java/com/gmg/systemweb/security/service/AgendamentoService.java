package com.gmg.systemweb.security.service;

import com.gmg.systemweb.security.datatables.Datatables;
import com.gmg.systemweb.security.datatables.DatatablesColunas;
import com.gmg.systemweb.security.domain.Agendamento;
import com.gmg.systemweb.security.domain.Horario;
import com.gmg.systemweb.security.exception.AcessoNegadoException;
import com.gmg.systemweb.security.repository.AgendamentoRepository;
import com.gmg.systemweb.security.repository.projection.HistoricoPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AgendamentoService {

	@Autowired
	private AgendamentoRepository repository;
	@Autowired
	private Datatables datatables;

	@Transactional(readOnly = true)
	public List<Horario> buscarHorariosNaoAgendadosPorMedicoIdEData(Long id, LocalDate data) {
		return repository.findByMedicoIdAndDataNotHorarioAgendado(id, data);
	}

	@Transactional(readOnly = false)
	public void salvar(Agendamento agendamento) {
		repository.save(agendamento);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarHistoricoPorPacienteEmail(String email, HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
		Page<HistoricoPaciente> page = repository.findHistoricoByPacienteEmail(email, datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> buscarHistoricoPorMedicoEmail(String email, HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
		Page<HistoricoPaciente> page = repository.findHistoricoByMedicoEmail(email, datatables.getPageable());
		return datatables.getResponse(page);
	}

	@Transactional(readOnly = true)
	public Agendamento buscarPorId(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
	}

	@Transactional(readOnly = false)
	public void editar(Agendamento agendamento, String email) {
		Agendamento ag = buscarPorIdEUsuario(agendamento.getId(), email);
		ag.setDataConsulta(agendamento.getDataConsulta());
		ag.setEspecialidade(agendamento.getEspecialidade());
		ag.setHorario(agendamento.getHorario());
		ag.setMedico(agendamento.getMedico());
	}

	@Transactional(readOnly = true)
	public Agendamento buscarPorIdEUsuario(Long id, String email) {
		return repository.findByIdAndPacienteOrMedicoEmail(id, email)
				.orElseThrow(() -> new AcessoNegadoException("Acesso negado ao usuário: " + email));
	}

	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
	}

	@Transactional(readOnly = false)
	public Agendamento salvarProntuario(Long id, String descricao, Boolean possuiPlanoSaude, Double valorConsulta, String email) {
		Agendamento agendamento = buscarPorIdEUsuario(id, email);
		agendamento.setDescricao(descricao);
		agendamento.setPossuiPlanoSaude(possuiPlanoSaude);
		agendamento.setValorConsulta(possuiPlanoSaude ? null : valorConsulta);
		return repository.save(agendamento);
	}

	@Transactional(readOnly = false)
	public Agendamento salvarAvaliacao(Long id, Integer nota, String comentario, String email) {
		Agendamento agendamento = buscarPorIdEUsuario(id, email);
		agendamento.setNotaAvaliacao(nota);
		agendamento.setComentarioAvaliacao(comentario);
		return repository.save(agendamento);
	}
}