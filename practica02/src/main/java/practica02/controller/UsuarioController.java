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
import practica02.service.RolService;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService,
                             RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    //Listado
    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.getUsuarios(false); // false = todos
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        return "usuario/listado"; // /templates/usuario/listado.html
    }

    //Crear
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.getRoles()); 
        return "usuario/form";
    }

    //Guardar (para crear o editar)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") Usuario usuario,
                          RedirectAttributes redirectAttributes) {
        try {
            // true → encriptar contraseña con BCrypt
            usuarioService.save(usuario, true);
            redirectAttributes.addFlashAttribute("todoOk",
                    "Usuario guardado correctamente.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo guardar el usuario: " + ex.getMessage());
        }
        return "redirect:/admin/usuarios/listado";
    }

    //Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long idUsuario,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "El usuario no fue encontrado.");
            return "redirect:/admin/usuarios/listado";
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setPassword(""); // para no mostrar el hash

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.getRoles());

        return "usuario/form";
    }

    //Eliminar
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long idUsuario,
                           RedirectAttributes redirectAttributes) {

        try {
            usuarioService.delete(idUsuario);
            redirectAttributes.addFlashAttribute("todoOk",
                    "Usuario eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error",
                    "El usuario no existe.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el usuario. Tiene datos asociados.");
        }

        return "redirect:/admin/usuarios/listado";
    }
}

