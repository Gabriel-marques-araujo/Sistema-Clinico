package com.gmg.systemweb.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "pacientes")
public class Paciente extends AbstractEntity {

	@Column(name = "nome", unique = true, nullable = false)
	private String nome;

	@Column(name = "idade_paciente", nullable = false)
	private int idadePaciente;

	@Column(name = "plano_de_saude")
	private int planoDeSaude;

	@JsonIgnore
	@OneToMany(mappedBy = "paciente")
	private List<Agendamento> agendamentos;

	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;




	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getIdadePaciente() {
		return idadePaciente;
	}

	public void setIdadePaciente(int idadePaciente) {
		this.idadePaciente = idadePaciente;
	}

	public List<Agendamento> getAgendamentos() {
		return agendamentos;
	}

	public void setAgendamentos(List<Agendamento> agendamentos) {
		this.agendamentos = agendamentos;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getPlanoDeSaude() {
		return planoDeSaude;
	}

	public void setPlanoDeSaude(int planoDeSaude) {
		this.planoDeSaude = planoDeSaude;
	}
}