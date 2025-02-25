package com.gmg.systemweb.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gmg.systemweb.security.domain.Medico;
import com.gmg.systemweb.security.repository.MedicoRepository;

@Service
public class MedicoService {

	@Autowired
	private MedicoRepository repository;

	@Transactional(readOnly = true)
	public Medico buscarPorUsuarioId(Long id) {

		return repository.findByUsuarioId(id).orElse(new Medico());
	}

	@Transactional(readOnly = false)
	public void salvar(Medico medico) {

		repository.save(medico);
	}

	@Transactional(readOnly = false)
	public void editar(Medico medico) {
		Medico m2 = repository.findById(medico.getId()).get();
		m2.setCrm(medico.getCrm());
		m2.setDtInscricao(medico.getDtInscricao());
		m2.setNome(medico.getNome());

		if(!medico.getEspecialidades().isEmpty()) {
			m2.getEspecialidades().addAll(medico.getEspecialidades());
		}
	}

	@Transactional(readOnly = true)
	public Medico buscarPorEmail(String email) {

		return repository.findByUsuarioEmail(email).orElse(new Medico());
	}

	@Transactional(readOnly = false)
	public void excluirEspecialidadePorMedico(Long idMed, Long idEsp) {
		Medico medico = repository.findById(idMed).get();
		medico.getEspecialidades().removeIf(e -> e.getId().equals(idEsp));
	}

	@Transactional(readOnly = true)
	public List<Medico> buscarMedicosPorEspecialidade(String titulo) {
		return repository.findByMedicosPorEspecialidade(titulo);
	}

	@Transactional(readOnly = true)
	public List<Medico> buscarMedicoPlano(String titulo, int plano){
		List<Medico> medicos = repository.findByMedicosPorEspecialidade(titulo);
		List<Medico> medicosComPlano = new ArrayList<>();

		if(plano == 0){
			return medicos;
		}else {
			for(Medico m:  medicos){
				if (m.getPlanoDeSaude() == plano){
					medicosComPlano.add(m);
				}
			}
		}


		return medicosComPlano;

	}


	@Transactional(readOnly = true)
	public boolean existeEspecialidadeAgendada(Long idMed, Long idEsp) {

		return repository.hasEspecialidadeAgendada(idMed, idEsp).isPresent();
	}
}
