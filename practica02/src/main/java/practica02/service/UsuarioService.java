/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package practica02.service;

/**
 *
 * @author sofic
 */
import practica02.domain.Rol;
import practica02.domain.Usuario;
import practica02.repository.RolRepository;
import practica02.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import java.time.LocalDate;
import java.time.LocalDateTime; // (por si este tampoco estuviera)




@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //>>> CRUD

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean soloActivos) {
        if (soloActivos) {
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public Usuario save(Usuario usuario, boolean encriptarClave) {
        final Long idUser = usuario.getId();

        //Validación de correo duplicado
        if (idUser == null) {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new DataIntegrityViolationException("El correo ya está en uso por otro usuario.");
            }
        } else {
            if (usuarioRepository.existsByEmailAndIdNot(usuario.getEmail(), idUser)) {
                throw new DataIntegrityViolationException("El correo ya está en uso por otro usuario.");
            }
        }

        //Manejo de contraseña
        if (usuario.getId() == null) {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para nuevos usuarios.");
            }
            if (encriptarClave) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        } else {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                Usuario usuarioExistente = usuarioRepository.findById(usuario.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Usuario a modificar no encontrado."));
                usuario.setPassword(usuarioExistente.getPassword());
            } else {
                if (encriptarClave) {
                    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                }
            }
        }

        //Rol por defecto
        if (usuario.getRol() == null) {
            Rol rolPorDefecto = rolRepository.findByNombre("ESTUDIANTE")
                    .orElseThrow(() -> new IllegalStateException("Rol por defecto 'ESTUDIANTE' no existe en la BD."));
            usuario.setRol(rolPorDefecto);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el usuario. Tiene datos asociados.", e);
        }
    }

    @Transactional
    public Usuario cambiarRol(Long idUsuario, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));

        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));

        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }

    //>>> CONSULTAS AVANZADAS

    //Carga usuarios ordenados por fecha de creación para el listado inicial
    @Transactional(readOnly = true)
    public List<Usuario> listarOrdenadosPorFecha() {
        return usuarioRepository.findAllOrderByFechaCreacionDesc();
    }

    //Consulta 1 por rol
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorRol(String nombreRol) {
        return usuarioRepository.findByRol_Nombre(nombreRol);
    }

    //Consulta 2 por rango de fechas
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            return List.of();
        }
        LocalDateTime desde = inicio.atStartOfDay();
        LocalDateTime hasta = fin.plusDays(1).atStartOfDay().minusSeconds(1);
        return usuarioRepository.findByFechaCreacionBetween(desde, hasta);
    }

    //Consulta 3 por coincidencia parcial en correo o nombre
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorTexto(String texto) {
        if (texto == null) {
            texto = "";
        }
        return usuarioRepository
                .findByEmailContainingIgnoreCaseOrNombreContainingIgnoreCase(texto, texto);
    }

    //Consulta 4: Resumen activos vs inactivos
    @Transactional(readOnly = true)
    public Map<Boolean, Long> obtenerConteoActivosInactivos() {
        Map<Boolean, Long> mapa = new HashMap<>();
        for (Object[] fila : usuarioRepository.conteoActivosInactivos()) {
            Boolean activo = (Boolean) fila[0];
            Long total = (Long) fila[1];
            mapa.put(activo, total);
        }
        return mapa;
    }
    
    
    //Consultas adicionales para mostrar algo en "Reportes"
    public long countActivos() { 
        return usuarioRepository.countByActivoTrue(); 
    }
    
    public long countInactivos() { 
        return usuarioRepository.countByActivoFalse(); 
    }

    public List<Map<String, Object>> countUsuariosPorRol() {
        var lista = usuarioRepository.resumenPorRol();
        List<Map<String, Object>> out = new ArrayList<>();

        for (Object[] row : lista) {
            Map<String, Object> m = new HashMap<>();
            m.put("nombre", row[0]);
            m.put("total", row[1]);
            out.add(m);
        }
        return out;
    }

    public List<Usuario> getUltimosUsuarios() {
        return usuarioRepository.findTop5ByOrderByIdDesc();
    }
    
}
