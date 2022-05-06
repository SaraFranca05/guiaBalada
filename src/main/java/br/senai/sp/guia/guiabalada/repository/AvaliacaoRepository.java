package br.senai.sp.guia.guiabalada.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;


import br.senai.sp.guia.guiabalada.model.Avaliacao;

public interface AvaliacaoRepository extends PagingAndSortingRepository<Avaliacao, Long>{
	public List<Avaliacao> findByBaladaId(Long idRest);
}
