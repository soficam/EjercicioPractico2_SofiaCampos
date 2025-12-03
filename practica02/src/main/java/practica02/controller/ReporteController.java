/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package practica02.controller;

/**
 *
 * @author sofic
 */
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import practica02.service.UsuarioService;

@Controller
@RequestMapping("/profesor/reportes")
public class ReporteController {

    private final UsuarioService usuarioService;

    public ReporteController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String reportes(Model model) {

        long activos = usuarioService.countActivos();
        long inactivos = usuarioService.countInactivos();
        var resumenRoles = usuarioService.countUsuariosPorRol();
        var ultimos = usuarioService.getUltimosUsuarios();

        model.addAttribute("activos", activos);
        model.addAttribute("inactivos", inactivos);
        model.addAttribute("resumenRoles", resumenRoles);
        model.addAttribute("ultimos", ultimos);

        return "profesor/reportes";
    }
}

