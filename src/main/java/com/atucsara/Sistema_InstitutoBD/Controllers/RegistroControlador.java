package com.atucsara.Sistema_InstitutoBD.Controllers;

import com.atucsara.Sistema_InstitutoBD.services.UsuarioServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistroControlador {

    @Autowired
    private UsuarioServicio servicio;

    @GetMapping("/login")
    public String iniciarSesion() {
        return "login";
    }

    @GetMapping("/")
    public String verPaginaDeInicio(Model modelo) {
        modelo.addAttribute("usuarios", servicio.listarUsuarios());
        return "index";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login?logout";
    }

}


