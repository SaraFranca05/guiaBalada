package br.senai.sp.guia.guiabalada.rest;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.json.webtoken.JsonWebToken;

import antlr.Token;
import br.senai.sp.guia.guiabalada.annotation.Privado;
import br.senai.sp.guia.guiabalada.annotation.Publico;
import br.senai.sp.guia.guiabalada.model.Erro;
import br.senai.sp.guia.guiabalada.model.TokenJWT;
import br.senai.sp.guia.guiabalada.model.Usuario;
import br.senai.sp.guia.guiabalada.repository.UsuarioRepository;


@RestController
@RequestMapping("api/usuario")
public class UsuarioRestController {
	public static final String EMISSOR = "SENAI";
	public static final String SECRET = "@551n@TuR@";
	
	@Autowired
	private UsuarioRepository repository;
	@Publico
	@RequestMapping(value = "", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> criarUsuario(@RequestBody Usuario usuario){
		try {
		//insere o usuario no banco de dados
		repository.save(usuario);
		return ResponseEntity.created(URI.create("/api/usuario/"+usuario.getId())).body(usuario);
		}catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			Erro erro = new Erro(HttpStatus.INTERNAL_SERVER_ERROR, "Registro duplicado", e.getClass().getName());
			return new ResponseEntity<Object>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
			}catch(Exception e) {
			e.printStackTrace();
			Erro erro = new Erro(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getClass().getName());
			return new ResponseEntity<Object>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}
	
	@Privado
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ResponseEntity<Usuario> getRestaurante(@PathVariable("id") Long id){
	//tenta buscar o restaurante no repository
	Optional<Usuario> optional = repository.findById(id);

	if (optional.isPresent()) {
	return ResponseEntity.ok(optional.get());
	}else {
	return ResponseEntity.notFound().build();
	}
	}
	
	@Privado
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> atualizarUsuario(@RequestBody Usuario usuario, @PathVariable("id") Long id){
		//valida????o do ID
		if(id != usuario.getId()) {
			throw new RuntimeException("ID Inv??lido");
			
		}
		repository.save(usuario);
		return ResponseEntity.ok().build();
	}
	@Privado
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> excluirUsuario(@PathVariable("id") Long idUsuario){
		repository.deleteById(idUsuario);
		return ResponseEntity.noContent().build();
	}
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	//buscar o usu??rio no banco de dados
	public ResponseEntity<TokenJWT> logar(@RequestBody Usuario usuario){
		usuario = repository.findByEmailAndSenha(usuario.getEmail(), usuario.getSenha());
		//verifica se o usuario ?? null
		if(usuario != null) {
			// variavel para inserir dados no payload
			Map<String, Object> payload = new HashMap<String, Object>();
			payload.put("id_user", usuario.getId());
			payload.put("name", usuario.getNome());
			//variavel para data de expira????o
			Calendar expiracao = Calendar.getInstance();
			//adiciona
			expiracao.add(Calendar.HOUR, 1);
			//algoritmo para assinar o token
			Algorithm algoritmo = Algorithm.HMAC256(SECRET);
			
			TokenJWT tokenJwt = new TokenJWT();
			tokenJwt.setToken(JWT.create()
					.withPayload(payload)
					.withIssuer(EMISSOR)
					.withExpiresAt(expiracao.getTime())
					.sign(algoritmo));
					return ResponseEntity.ok(tokenJwt);
					}else {
					return new ResponseEntity<TokenJWT>(HttpStatus.UNAUTHORIZED);
					}
		}
	}
		
	

