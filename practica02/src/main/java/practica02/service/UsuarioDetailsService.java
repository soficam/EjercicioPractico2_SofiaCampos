/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package practica02.service;

/**
 *
 * @author sofic
 */
import practica02.domain.Usuario;
import practica02.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final HttpSession session;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository,
                                 HttpSession session) {
        this.usuarioRepository = usuarioRepository;
        this.session = session;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        //Usamos el email como "username"
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + username));

        session.removeAttribute("nombreUsuario");
        session.setAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());

        String rolNombre = usuario.getRol().getNombre(); // ADMIN / PROFESOR / ESTUDIANTE
        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + rolNombre)
        );

        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}
