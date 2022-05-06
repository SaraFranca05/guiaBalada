package br.senai.sp.guia.guiabalada.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.senai.sp.guia.guiabalada.model.Balada;

import br.senai.sp.guia.guiabalada.repository.BaladaRepository;
import br.senai.sp.guia.guiabalada.repository.TipoBaladaRepository;
import br.senai.sp.guia.guiabalada.util.FirebaseUtil;

@Controller
public class BaladaController {
	@Autowired
	private TipoBaladaRepository repTipo;
	@Autowired
	private BaladaRepository repository;
	@Autowired
	private FirebaseUtil fireUtil;
	
	@RequestMapping("formBalada")
	public String form( Model model) {
		model.addAttribute("tipos", repTipo.findAllByOrderByNomeAsc());
		return "formBalada";
	}
	@RequestMapping("salvarBalada")
	public String salvar(Balada balada, @RequestParam("fileFotos") MultipartFile [] fileFotos) {
		//String para armazenar as URLs
		String fotos = balada.getFotos();
		//percorre cara arquivo no vetor
		for(MultipartFile arquivo : fileFotos) {
			//verifica se o arquivo existe
			if(arquivo.getOriginalFilename().isEmpty()) {
				//vai para o próximo arquivo
				continue;
			}
			try {
				//faz o upload e guarda na URL na String fotos
				fotos += fireUtil.upload(arquivo)+";";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		//guarda na variavel Balada as fotos
		balada.setFotos(fotos);
		//salva no BD
		repository.save(balada);
		
		return "redirect:formBalada";
	}
	@RequestMapping("listaBalada/{page}")
	public String listaBalada(Model model, @PathVariable("page") int page) {
		// criar um pageable informando os parâmetros da página
		PageRequest pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		// crir um page de TipoBlada através dos parâmetros passados ao repository
		Page<Balada> pagina = repository.findAll(pageable);
		// Adiciona a model, a lista com os admins
		model.addAttribute("balada", pagina.getContent());
		// variavel para o total de paginas
		int totalPages = pagina.getTotalPages();
		// cria um list de inteiros para armazenas os numeros das páginas
		List<Integer> numPaginas = new ArrayList<Integer>();
		// preencher o list com as páginas
		for (int i = 1; i <= totalPages; i++) {
			// adiciona a página ao lista
			numPaginas.add(i);
		}
		// adiciona os valores á model
		model.addAttribute("numPaginas", numPaginas);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("pagAtual", page);
		// retorna para html da lista
		return "listaBalada";
	}

	@RequestMapping("alterarBalada")
	public String alterarBalada(Long id, Model model) {
		Balada balada = repository.findById(id).get();
		model.addAttribute("balada", balada);
		return "forward:formBalada";
	}

	@RequestMapping("excluirBalada")
	public String excluirBalada(Long id) {
		Balada balada = repository.findById(id).get();
		if(balada.getFotos().length() > 0) {
			for(String foto : balada.verFotos()) {
				fireUtil.deletar(foto);
			}
		}
		repository.delete(balada);
		return "redirect:/listaBalada/1";
	}
	@RequestMapping("excluirFotos")
	public String excluirFoto(Long idBalada, int numFotos, Model model) {
		//buscar a balada no BD
		Balada balada= repository.findById(idBalada).get();
		//busca URL da foto
		String urlFotos = balada.verFotos()[numFotos];
		//deleta foto
		fireUtil.deletar(urlFotos);
		//remove URL do atributo fotos
		balada.setFotos(balada.getFotos().replace(urlFotos+";", ""));
		//salva no BD
		repository.save(balada);
		//coloca o balada na Model
		model.addAttribute("balada", balada);
		return "forward:formBalada";
		
	}
	
}
