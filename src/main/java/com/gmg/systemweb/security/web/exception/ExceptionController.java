package com.gmg.systemweb.security.web.exception;

import javax.mail.MessagingException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.gmg.systemweb.security.exception.AcessoNegadoException;

@ControllerAdvice
public class ExceptionController {
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ModelAndView usuarioNaoEncontradoException(UsernameNotFoundException ex) {
		ModelAndView model  = new ModelAndView("error");
		model.addObject("st 	atus", 404);
		model.addObject("error", "Operação não pode ser realizada");
		model.addObject("message", ex.getMessage());
		return model;
	}
	
	@ExceptionHandler(AcessoNegadoException.class)
	public ModelAndView acessoNegadoException(AcessoNegadoException ex) {
		ModelAndView model = new ModelAndView("error");
		model.addObject("status", 403);
		model.addObject("error", "Operação não pode ser realizada.");
		model.addObject("message", ex.getMessage());
		return model;
	}
}
 	