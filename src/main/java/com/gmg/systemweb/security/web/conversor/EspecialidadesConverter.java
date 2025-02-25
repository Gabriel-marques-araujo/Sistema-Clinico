package com.gmg.systemweb.security.web.conversor;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.gmg.systemweb.security.domain.Especialidade;
import com.gmg.systemweb.security.service.EspecialidadeService;

@Component
public class EspecialidadesConverter implements Converter<String[], Set<Especialidade>> {

	@Autowired
	private EspecialidadeService service;

	@Override
	public Set<Especialidade> convert(String[] titulos) {
		
		Set<Especialidade> especialidades = new HashSet<>();
		if (titulos != null && titulos.length > 0) {
			especialidades.addAll(service.buscarPorTitulos(titulos));			
		}
		return especialidades;
	}
}

