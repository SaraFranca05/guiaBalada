package br.senai.sp.guia.guiabalada.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.guia.guiabalada.annotation.Publico;
import br.senai.sp.guia.guiabalada.model.Balada;
import br.senai.sp.guia.guiabalada.repository.BaladaRepository;


@RestController
@RequestMapping("/api/balada")
public class BaladaRestController {
	
	@Autowired
	private BaladaRepository repository;
	@Publico
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Iterable <Balada> getBalada(){
		
		return repository.findAll();
	}
	@Publico
	@RequestMapping(value="/{id}")
	public ResponseEntity<Balada> getBalada(@PathVariable("id") Long idBalada){
		//tenta buscar balada no repository
		Optional<Balada> optional = repository.findById(idBalada);
		// se a balada existir
		if(optional.isPresent()) {
			return ResponseEntity.ok(optional.get());
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	@Publico
	@RequestMapping(value="/tipo/{id}", method = RequestMethod.GET)
	public List <Balada> getTipoBaladas(@PathVariable("id")Long idTipo){
		return repository.buscarPorTipo(idTipo);
		
	}
	
}

