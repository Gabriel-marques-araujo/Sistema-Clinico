package com.gmg.systemweb.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.gmg.systemweb.security.domain.PerfilTipo;
import com.gmg.systemweb.security.service.UsuarioService;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity 
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
    private static final String MEDICO = PerfilTipo.MEDICO.getDesc();
    private static final String PACIENTE = PerfilTipo.PACIENTE.getDesc();
	
	@Autowired
	private UsuarioService service;


	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.csrf().disable() // Desativa CSRF para APIs REST
				.authorizeRequests()
				// acessos públicos liberados
				.antMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll()
				.antMatchers("/", "/home").permitAll()
				.antMatchers("/u/novo/cadastro", "/u/cadastro/realizado", "/u/cadastro/paciente/salvar").permitAll()
				.antMatchers("/u/confirmacao/cadastro").permitAll()
				.antMatchers("/u/p/**").permitAll()
				.antMatchers("/medicos/especialidade/titulo-plano/**").permitAll()
				.antMatchers("/medicos/email/**").permitAll()

				// acesso público a um endpoint específico
				.antMatchers("/consultas/salvar").permitAll()

				// acessos privados admin
				.antMatchers("/u/editar/senha", "/u/confirmar/senha").hasAnyAuthority(PACIENTE, MEDICO)
				.antMatchers("/u/**").hasAuthority(ADMIN)

				// acessos privados medicos
				.antMatchers("/medicos/especialidade/titulo/*").hasAnyAuthority(PACIENTE, MEDICO)
				.antMatchers("/medicos/dados", "/medicos/salvar", "/medicos/editar").hasAnyAuthority(MEDICO, ADMIN)
				.antMatchers("/medicos/**").hasAuthority(MEDICO)

				// acessos privados pacientes
				.antMatchers("/pacientes/**").hasAuthority(PACIENTE)

				// acessos privados especialidades
				.antMatchers("/especialidades/datatables/server/medico/*").hasAnyAuthority(MEDICO, ADMIN)
				.antMatchers("/especialidades/titulo").hasAnyAuthority(MEDICO, ADMIN, PACIENTE)
				.antMatchers("/especialidades/**").hasAuthority(ADMIN)

				.anyRequest().authenticated()  // todas as demais rotas precisam estar autenticadas
				.and()
				.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.failureUrl("/login-error")
				.permitAll()
				.and()
				.logout()
				.logoutSuccessUrl("/")
				.and()
				.exceptionHandling()
				.accessDeniedPage("/acesso-negado")
				.and()
				.rememberMe();
	}


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());


	}

	

}