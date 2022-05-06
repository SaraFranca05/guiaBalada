package br.senai.sp.guia.guiabalada.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.senai.sp.guia.guiabalada.model.TipoBalada;
import br.senai.sp.guia.guiabalada.repository.TipoBaladaRepository;

@Controller
public class TipoBaladaController {
	@Autowired
	private TipoBaladaRepository repository;

	@RequestMapping(value = "cadastroTipo", method = RequestMethod.GET)
	public String tipo() {
		return "formTipoBalada";
	}

	@RequestMapping(value = "salvarTipo", method = RequestMethod.POST)
	public String salvarTipo(@Valid TipoBalada tipo, BindingResult result, RedirectAttributes attr) {
		// verifica se houve error na validacao
		if (result.hasErrors()) {
			attr.addFlashAttribute("mensagemErro", "Verifique os campos...");
			// redireciona para o formulario
			return "redirect:cadastroTipo";
		}
		try {
			// salva no bd a entidade
			repository.save(tipo);
			attr.addFlashAttribute("mensagemSucesso",
					"Administrador cadastrado com sucesso. Caso não informou a senha será a primeira parte do email. ID: "
							+ tipo.getId());

		} catch (Exception e) {
			// adiciona uma mensagem de sucesso
			attr.addFlashAttribute("mensagemErro", "Houve um erro ao cadastrar." + e.getMessage());
		}

		// redireciona para o formulario
		return "redirect:cadastroTipo";

	}

	@RequestMapping("listaTipo/{page}")
	public String listaTipo(Model model, @PathVariable("page") int page) {
		// criar um pageable informando os parâmetros da página
		PageRequest pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		// crir um page de TipoBlada através dos parâmetros passados ao repository
		Page<TipoBalada> pagina = repository.findAll(pageable);
		// Adiciona a model, a lista com os admins
		model.addAttribute("tipo", pagina.getContent());
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
		return "listaTipo";
	}

	@RequestMapping("alterarTipo")
	public String alterarTipo(Long id, Model model) {
		TipoBalada tipo = repository.findById(id).get();
		model.addAttribute("tipo", tipo);
		return "forward:cadastroTipo";
	}

	@RequestMapping("excluirTipo")
	public String excluirTipo(Long id) {
		repository.deleteById(id);
		return "redirect:listaTipo";
	}

	@RequestMapping("buscarPorTudo")
	public String buscarPorTudo(String select, String opcao, Model model) {

		if (select == null) {
			model.addAttribute("tipo", repository.buscarPorChave(opcao));

		} else if (select.equals("nome")) {
			model.addAttribute("tipo", repository.buscarPorNome(opcao));
		} else if (select.equals("faixaEtaria")) {
			model.addAttribute("tipo", repository.buscarPorFaixa(opcao));
		} else {
			model.addAttribute("tipo", repository.buscarPorChave(opcao));
		}
		return "listaTipo";
	}

}
