/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package practica02.controller;

/**
 *
 * @author sofic
 */
import practica02.domain.Rol;
import practica02.service.RolService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var roles = rolService.getRoles();
        model.addAttribute("roles", roles);
        return "rol/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("rol", new Rol());
        return "rol/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long idRol,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        Optional<Rol> rolOpt = rolService.getRol(idRol);
        if (rolOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "El rol no fue encontrado.");
            return "redirect:/admin/roles/listado";
        }
        model.addAttribute("rol", rolOpt.get());
        return "rol/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("rol") Rol rol,
                          RedirectAttributes redirectAttributes) {
        rolService.save(rol);
        redirectAttributes.addFlashAttribute("todoOk",
                "Rol guardado correctamente.");
        return "redirect:/admin/roles/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long idRol,
                           RedirectAttributes redirectAttributes) {
        try {
            rolService.delete(idRol);
            redirectAttributes.addFlashAttribute("todoOk",
                    "Rol eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el rol.");
        }
        return "redirect:/admin/roles/listado";
    }
}