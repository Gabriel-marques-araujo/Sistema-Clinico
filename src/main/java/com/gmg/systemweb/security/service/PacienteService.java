package com.gmg.systemweb.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gmg.systemweb.security.domain.Paciente;
import com.gmg.systemweb.security.repository.PacienteRepository;

@Service
public class PacienteService {

	@Autowired
	private PacienteRepository repository;

	@Transactional(readOnly = true)
	public Paciente buscarPorUsuarioEmail(String email) {
		return repository.findByUsuarioEmail(email).orElse(new Paciente());
	}

	@Transactional(readOnly = false)
	public void salvar(Paciente paciente) {

		repository.save(paciente);
	}

	@Transactional(readOnly = false)
	public void editar(Paciente paciente) {
		Paciente p2 = repository.findById(paciente.getId()).get();
		p2.setNome(paciente.getNome());
		p2.setIdadePaciente(paciente.getIdadePaciente());
		p2.setPlanoDeSaude(paciente.getPlanoDeSaude());

	}
}
