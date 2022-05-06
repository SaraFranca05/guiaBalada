package br.senai.sp.guia.guiabalada.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.senai.sp.guia.guiabalada.model.Administrador;


public interface AdminRepository extends PagingAndSortingRepository<Administrador, Long>{
	public Administrador findByEmailAndSenha(String email, String senha);
}
