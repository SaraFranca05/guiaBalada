package br.senai.sp.guia.guiabalada.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.senai.sp.guia.guiabalada.model.Usuario;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long>{
	public Usuario findByEmailAndSenha(String email, String senha);
}
