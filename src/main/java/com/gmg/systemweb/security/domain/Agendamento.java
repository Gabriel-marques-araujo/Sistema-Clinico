package com.gmg.systemweb.security.domain;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "agendamentos")
public class Agendamento extends AbstractEntity {

	@ManyToOne
	@JoinColumn(name = "id_especialidade")
	private Especialidade especialidade;

	@ManyToOne
	@JoinColumn(name = "id_medico")
	private Medico medico;

	@ManyToOne
	@JoinColumn(name = "id_paciente")
	private Paciente paciente;

	@ManyToOne
	@JoinColumn(name = "id_horario")
	private Horario horario;

	@Column(name = "data_consulta")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate dataConsulta;

	// Campos do prontuário
	@Column(name = "descricao", length = 1000)
	private String descricao;

	@Column(name = "possui_plano_saude")
	private Boolean possuiPlanoSaude;

	@Column(name = "valor_consulta")
	private Double valorConsulta;

	// Campos da avaliação
	@Column(name = "nota_avaliacao")
	private Integer notaAvaliacao;

	@Column(name = "comentario_avaliacao", length = 500)
	private String comentarioAvaliacao;

	// Getters e Setters
	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public Medico getMedico() {
		return medico;
	}

	public void setMedico(Medico medico) {
		this.medico = medico;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Horario getHorario() {
		return horario;
	}

	public void setHorario(Horario horario) {
		this.horario = horario;
	}

	public LocalDate getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(LocalDate dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getPossuiPlanoSaude() {
		return possuiPlanoSaude;
	}

	public void setPossuiPlanoSaude(Boolean possuiPlanoSaude) {
		this.possuiPlanoSaude = possuiPlanoSaude;
	}

	public Double getValorConsulta() {
		return valorConsulta;
	}

	public void setValorConsulta(Double valorConsulta) {
		this.valorConsulta = valorConsulta;
	}

	public Integer getNotaAvaliacao() {
		return notaAvaliacao;
	}

	public void setNotaAvaliacao(Integer notaAvaliacao) {
		this.notaAvaliacao = notaAvaliacao;
	}

	public String getComentarioAvaliacao() {
		return comentarioAvaliacao;
	}

	public void setComentarioAvaliacao(String comentarioAvaliacao) {
		this.comentarioAvaliacao = comentarioAvaliacao;
	}
}