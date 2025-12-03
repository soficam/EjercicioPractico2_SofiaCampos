/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package practica02.controller;

/**
 *
 * @author sofic
 */
import practica02.domain.Usuario;
import practica02.service.UsuarioService;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/estudiante")
public class PerfilController {

    private final UsuarioService usuarioService;

    public PerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public String verPerfil(Authentication authentication, Model model) {
        // El username es el email
        String email = authentication.getName();

        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioPorEmail(email);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login?error=true";
        }

        model.addAttribute("usuario", usuarioOpt.get());
        return "estudiante/perfil"; // /templates/estudiante/perfil.html
    }
}

