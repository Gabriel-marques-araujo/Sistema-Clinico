package com.gmg.systemweb.security.web.controller;

import com.gmg.systemweb.security.domain.Agendamento;
import com.gmg.systemweb.security.domain.Especialidade;
import com.gmg.systemweb.security.domain.Paciente;
import com.gmg.systemweb.security.domain.PerfilTipo;
import com.gmg.systemweb.security.service.AgendamentoService;
import com.gmg.systemweb.security.service.EspecialidadeService;
import com.gmg.systemweb.security.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Controller
@RequestMapping("agendamentos")
public class AgendamentoController {

	@Autowired
	private AgendamentoService service;
	@Autowired
	private PacienteService pacienteService;
	@Autowired
	private EspecialidadeService especialidadeService;

	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping({"/agendar"})
	public String agendarConsulta(Agendamento agendamento) {
		return "agendamento/cadastro";
	}

	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping("/horario/medico/{id}/data/{data}")
	public ResponseEntity<?> getHorarios(@PathVariable("id") Long id,
										 @PathVariable("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
		return ResponseEntity.ok(service.buscarHorariosNaoAgendadosPorMedicoIdEData(id, data));
	}

	@PreAuthorize("hasAuthority('PACIENTE')")
	@PostMapping({"/salvar"})
	public String salvar(Agendamento agendamento, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		Paciente paciente = pacienteService.buscarPorUsuarioEmail(user.getUsername());
		String titulo = agendamento.getEspecialidade().getTitulo();
		Especialidade especialidade = especialidadeService.buscarPorTitulos(new String[]{titulo}).stream().findFirst().get();
		agendamento.setEspecialidade(especialidade);
		agendamento.setPaciente(paciente);
		service.salvar(agendamento);
		attr.addFlashAttribute("sucesso", "Sua consulta foi agendada com sucesso.");
		return "redirect:/agendamentos/agendar";
	}

	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping({"/historico/paciente", "/historico/consultas"})
	public String historico() {
		return "agendamento/historico-paciente";
	}

	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping("/datatables/server/historico")
	public ResponseEntity<?> historicoAgendamentosPorPaciente(HttpServletRequest request, @AuthenticationPrincipal User user) {
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.PACIENTE.getDesc()))) {
			return ResponseEntity.ok(service.buscarHistoricoPorPacienteEmail(user.getUsername(), request));
		}
		if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.MEDICO.getDesc()))) {
			return ResponseEntity.ok(service.buscarHistoricoPorMedicoEmail(user.getUsername(), request));
		}
		return ResponseEntity.notFound().build();
	}

	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping("/editar/consulta/{id}")
	public String preEditarConsultaPaciente(@PathVariable("id") Long id, ModelMap model, @AuthenticationPrincipal User user) {
		Agendamento agendamento = service.buscarPorIdEUsuario(id, user.getUsername());
		model.addAttribute("agendamento", agendamento);
		return "agendamento/cadastro";
	}

	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@PostMapping("/editar")
	public String editarConsulta(Agendamento agendamento, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		String titulo = agendamento.getEspecialidade().getTitulo();
		Especialidade especialidade = especialidadeService.buscarPorTitulos(new String[]{titulo}).stream().findFirst().get();
		agendamento.setEspecialidade(especialidade);
		service.editar(agendamento, user.getUsername());
		attr.addFlashAttribute("sucesso", "Sua consulta foi alterada com sucesso.");
		return "redirect:/agendamentos/agendar";
	}

	@PreAuthorize("hasAuthority('PACIENTE')")
	@GetMapping("/excluir/consulta/{id}")
	public String excluirConsulta(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("sucesso", "Consulta excluída com sucesso.");
		return "redirect:/agendamentos/historico/paciente";
	}

	@PreAuthorize("hasAuthority('MEDICO')")
	@PostMapping("/prontuario/{id}")
	public ResponseEntity<Agendamento> salvarProntuario(
			@PathVariable("id") Long id,
			@RequestBody ProntuarioDTO prontuarioDTO,
			@AuthenticationPrincipal User user) {
		Agendamento agendamento = service.salvarProntuario(
				id,
				prontuarioDTO.getDescricao(),
				prontuarioDTO.getPossuiPlanoSaude(),
				prontuarioDTO.getValorConsulta(),
				user.getUsername()
		);
		return ResponseEntity.ok(agendamento);
	}

	@PreAuthorize("hasAuthority('PACIENTE')")
	@PostMapping("/avaliacao/{id}")
	public ResponseEntity<Agendamento> salvarAvaliacao(
			@PathVariable("id") Long id,
			@RequestBody AvaliacaoDTO avaliacaoDTO,
			@AuthenticationPrincipal User user) {
		Agendamento agendamento = service.salvarAvaliacao(
				id,
				avaliacaoDTO.getNota(),
				avaliacaoDTO.getComentario(),
				user.getUsername()
		);
		return ResponseEntity.ok(agendamento);
	}

	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping("/{id}")
	public ResponseEntity<Agendamento> buscarAgendamento(@PathVariable("id") Long id, @AuthenticationPrincipal User user) {
		Agendamento agendamento = service.buscarPorIdEUsuario(id, user.getUsername());
		return ResponseEntity.ok(agendamento);
	}
}

class ProntuarioDTO {
	private String descricao;
	private Boolean possuiPlanoSaude;
	private Double valorConsulta;

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public Boolean getPossuiPlanoSaude() { return possuiPlanoSaude; }
	public void setPossuiPlanoSaude(Boolean possuiPlanoSaude) { this.possuiPlanoSaude = possuiPlanoSaude; }
	public Double getValorConsulta() { return valorConsulta; }
	public void setValorConsulta(Double valorConsulta) { this.valorConsulta = valorConsulta; }
}

class AvaliacaoDTO {
	private Integer nota;
	private String comentario;

	public Integer getNota() { return nota; }
	public void setNota(Integer nota) { this.nota = nota; }
	public String getComentario() { return comentario; }
	public void setComentario(String comentario) { this.comentario = comentario; }
}