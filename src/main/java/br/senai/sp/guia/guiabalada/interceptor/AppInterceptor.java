package br.senai.sp.guia.guiabalada.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import br.senai.sp.guia.guiabalada.annotation.Publico;
@Component
public class AppInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//variavel para obter a URI
		String uri = request.getRequestURI();
		//variavel para a sessão
		HttpSession session = request.getSession();
		//se for página de erro , libera
		if(uri.startsWith("/error")) {
			return true;
		}
		//verifical se handler é um handlerMethod, o que indica que ele esta chamando um método em algum controller
		if(handler instanceof HandlerMethod) {
			// casting de objet para Handler Method
			HandlerMethod metodo = (HandlerMethod) handler;
			if(uri.startsWith("/api")){
				
				return true;
			}else {
				
			//verifica se esse metodo é público
			if (metodo.getMethodAnnotation(Publico.class) != null) {
				return true;
			}
			//veriifca se existe um usuario logado
			if(session.getAttribute("usuarioLogado") != null) {
				return true;
			}
			//redireciona para página Inicial
			response.sendRedirect("/");
			return false;		
		  }
	  }
		
		return true;
	}
}
