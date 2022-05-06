 package br.senai.sp.guia.guiabalada.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import br.senai.sp.guia.guiabalada.model.TipoBalada;


public interface TipoBaladaRepository extends PagingAndSortingRepository<TipoBalada, Long>{
	
	
	@Query("SELECT t FROM TipoBalada t WHERE t.nome Like %:n%")
	public List <TipoBalada> buscarPorNome(@Param("n")String nome);
	
	@Query("SELECT t FROM TipoBalada t WHERE t.faixaEtaria Like %:n%")
	public List <TipoBalada> buscarPorFaixa(@Param("n")String faixaEtaria);
	
	@Query("SELECT t FROM TipoBalada t WHERE t.palavrasChave Like %:n%")
	public List <TipoBalada> buscarPorChave(@Param("n")String palavrasChave);
	
	public List<TipoBalada> findAllByOrderByNomeAsc();
}
