/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package practica02.controller;

/**
 *
 * @author sofic
 */
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import practica02.service.RolService;
import practica02.service.UsuarioService;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public ConsultaController(UsuarioService usuarioService,
                              RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.listarOrdenadosPorFecha();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", rolService.getRoles());
        model.addAttribute("resumen", usuarioService.obtenerConteoActivosInactivos());
        return "consultas/listado";
    }

    //Consulta por rol
    @PostMapping("/porRol")
    public String consultaPorRol(@RequestParam("nombreRol") String nombreRol,
                                 Model model) {
        var usuarios = usuarioService.buscarPorRol(nombreRol);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", rolService.getRoles());
        model.addAttribute("filtroRol", nombreRol);
        model.addAttribute("resumen", usuarioService.obtenerConteoActivosInactivos());
        return "consultas/listado";
    }

    //Consulta por rango de fechas
    @PostMapping("/porFechas")
    public String consultaPorFechas(
            @RequestParam("fechaInicio")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        var usuarios = usuarioService.buscarPorRangoFechas(fechaInicio, fechaFin);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", rolService.getRoles());
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("resumen", usuarioService.obtenerConteoActivosInactivos());
        return "consultas/listado";
    }

    //Consulta por texto (correo o nombre contiene)
    @PostMapping("/porTexto")
    public String consultaPorTexto(@RequestParam("texto") String texto,
                                   Model model) {
        var usuarios = usuarioService.buscarPorTexto(texto);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", rolService.getRoles());
        model.addAttribute("texto", texto);
        model.addAttribute("resumen", usuarioService.obtenerConteoActivosInactivos());
        return "consultas/listado";
    }
}
