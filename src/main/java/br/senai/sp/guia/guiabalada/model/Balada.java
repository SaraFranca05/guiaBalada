package br.senai.sp.guia.guiabalada.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Balada {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	@Column(columnDefinition = "TEXT")
	private String descricao;
	private String cep;
	private String endereco;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String estado;
	private String atracoes;
	private String formadePagamento;
	private String horario;
	private String site;
	private String telefone;
	private boolean openbar;
	private boolean camarote;
	private boolean acessibilidade;
	private boolean estacionamento;
	private boolean areadeFumante;
	@ManyToOne
	private TipoBalada tipo;
	@Column(columnDefinition = "TEXT")
	private String fotos;
	private int preco;
	@OneToMany(mappedBy = "balada")
	private List<Avaliacao> avaliacoes;
	
	//retorna as fotos no vetr de String
	public String[] verFotos(){
		return fotos.split(";");
	}
	
}
