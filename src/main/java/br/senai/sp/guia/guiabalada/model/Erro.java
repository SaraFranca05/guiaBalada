package br.senai.sp.guia.guiabalada.model;

import org.apache.http.HttpStatus;

import lombok.Data;

@Data
public class Erro {
	
	private org.springframework.http.HttpStatus statusCode;
	private String mensagem;
	private String exception;
	
	public Erro(org.springframework.http.HttpStatus status, String msg, String exc) {
		this.statusCode = status;
		this.mensagem = msg;
		this.exception = exc;
	}
}
