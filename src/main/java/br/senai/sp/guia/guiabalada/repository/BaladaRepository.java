package br.senai.sp.guia.guiabalada.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import br.senai.sp.guia.guiabalada.model.Balada;

public interface BaladaRepository extends PagingAndSortingRepository<Balada, Long> {
	@Query("SELECT t FROM Balada t WHERE t.tipo.id = :n")
	public List <Balada> buscarPorTipo(@Param("n")Long id);
}
