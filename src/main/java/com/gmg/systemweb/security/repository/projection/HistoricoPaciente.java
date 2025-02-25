package com.gmg.systemweb.security.repository.projection;

import com.gmg.systemweb.security.domain.Especialidade;
import com.gmg.systemweb.security.domain.Medico;
import com.gmg.systemweb.security.domain.Paciente;

public interface HistoricoPaciente {
	
	Long getId();
	
	Paciente getPaciente();
	
	String getDataConsulta();
	
	Medico getMedico();
	
	Especialidade getEspecialidade();
}
