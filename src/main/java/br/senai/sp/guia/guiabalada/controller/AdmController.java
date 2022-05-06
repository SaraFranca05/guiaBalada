package br.senai.sp.guia.guiabalada.controller;

import java.util.ArrayList;

import java.util.List;

import javax.servlet.http.HttpSession;
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

import com.google.api.Http;

import br.senai.sp.guia.guiabalada.annotation.Publico;
import br.senai.sp.guia.guiabalada.model.Administrador;
import br.senai.sp.guia.guiabalada.repository.AdminRepository;
import br.senai.sp.guia.guiabalada.util.HashUtil;




@Controller
public class AdmController {
	
	//variavael para persistencia dos dados
	@Autowired
	private AdminRepository repository;
	
	@Publico
	//request mapping do formulario, do tipo Get
	@RequestMapping(value = "cadastroAdm", method = RequestMethod.GET)
	public String form() {
		return "formAdm";
	}
	@Publico
	//request mapping para salvar o Adminstrador, do tipo Post
	@RequestMapping(value= "salvarAdmin", method = RequestMethod.POST)
	public String salvarAdmin(@Valid Administrador admin, BindingResult result, RedirectAttributes attr) {
		//verifica se houve error na validacao
		if(result.hasErrors()) {
			attr.addFlashAttribute("mensagemErro", "Verifique os campos...");
			//redireciona para o formulario
			return "redirect:cadastroAdm";
		}
		
		// variavel para descobrir a alteração ou inserção
		boolean alteracao = admin.getId() != null ? true : false;
		//verificar se a senha esta vazia
		if(admin.getSenha().equals(HashUtil.hash(""))) {
			if(!alteracao) {
			//retirar a parte antes do @
			String parte = admin.getEmail().substring(0,admin.getEmail().indexOf("@"));
			//"setar" a parte na senha do admin
			admin.setSenha(parte);
			}else {
				//buscar senha atual no banco
				String hash = repository.findById(admin.getId()).get().getSenha();
				//"setar" o hash na senha 
				admin.setSenhaComHash(hash);
			}
		}
		try {
		//salva no bd a entidade
		repository.save(admin);
		attr.addFlashAttribute("mensagemSucesso", "Administrador cadastrado com sucesso. Caso não informou a senha será a primeira parte do email. ID: "+admin.getId());
		
		}catch (Exception e) {
			//adiciona uma mensagem de sucesso
			attr.addFlashAttribute("mensagemErro", "Houve um erro ao cadastrar."+e.getMessage());
		}
		
		//redireciona para o formulario
		return "redirect:cadastroAdm";
		
	}
	@Publico
	//request mapping para listar administrador
	@RequestMapping("listaAdm/{page}")
	public String listaAdm(Model model, @PathVariable("page") int page) {
		//criar um pageable informando os parâmetros da página
		PageRequest pageable = PageRequest.of(page-1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		//crir um page de Administrador através dos parâmetros passados ao repository
		Page<Administrador> pagina = repository.findAll(pageable);
		//Adiciona a model, a lista com os admins
		model.addAttribute("admins", pagina.getContent());
		// variavel para o total de paginas
		int totalPages = pagina.getTotalPages();
		//cria um list de inteiros para armazenas os numeros das páginas
		List<Integer> numPaginas = new ArrayList<Integer>();
		//preencher o list com as páginas
		for (int i = 1; i <= totalPages;i++){
			//adiciona a página ao lista
			numPaginas.add(i);
		}
		//adiciona os valores á model
		model.addAttribute("numPaginas",numPaginas );
		model.addAttribute("totalPages", totalPages );
		model.addAttribute("pagAtual", page );
		//retorna para html da lista
		return "listaAdm";
		}
	@Publico
	@RequestMapping("alterarAdm")
	public String alterar(Long id, Model model){
		Administrador adm  = repository.findById(id).get();
		model.addAttribute("admins", adm );
		return "forward:cadastroAdm";
	}
	@Publico
	@RequestMapping("excluirAdm")
	public String excluir(Long id) {
		repository.deleteById(id);
		return "redirect:listaAdm/1";
	}
	@Publico
	@RequestMapping("login")
	public String login(Administrador admLogin, RedirectAttributes attr, HttpSession session ) {
		//busca o Adm no BD
		Administrador admin = repository.findByEmailAndSenha(admLogin.getEmail(), admLogin.getSenha());
		if(admin == null) {
			attr.addFlashAttribute("mensagemErro", "Login e/ou Senha inválidos(s)");
			return "redirect:/";
		}else {
			//salvar o administrador na sesão
			session.setAttribute("usuarioLogado", admin);
			return "redirect:listaBalada/1";
		}
	}
	@Publico
	@RequestMapping("logout")
	public String logou(HttpSession session) {
		//invalida a sessão
		session.invalidate();
		//voltar para página inicial
		return "redirect:/";
	}
}
