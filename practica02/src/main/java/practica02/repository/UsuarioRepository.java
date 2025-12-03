/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package practica02.repository;

/**
 *
 * @author sofic
 */
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practica02.domain.Usuario;
import java.time.LocalDateTime;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByActivoTrue();

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndActivoTrue(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    //>>> CONSULTAS

    //Buscar usuarios por nombre de rol
    List<Usuario> findByRol_Nombre(String nombreRol);
    //Usuarios creados en un rango de fechas
    List<Usuario> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    //Buscar correo o nombre
    List<Usuario> findByEmailContainingIgnoreCaseOrNombreContainingIgnoreCase(
            String email, String nombre);

    // Contar activos vs inactivos
    @Query("SELECT u.activo, COUNT(u) FROM Usuario u GROUP BY u.activo")
    List<Object[]> conteoActivosInactivos();
    // Todos ordenados por fecha de creación desc
    @Query("SELECT u FROM Usuario u ORDER BY u.fechaCreacion DESC")
    List<Usuario> findAllOrderByFechaCreacionDesc();
    
    
    //Consultas adicionales para mostrar en reportes:
    //Mostrar activos e inactivos
    long countByActivoTrue();
    long countByActivoFalse();

    //Mostrar conteo agrupado por rol
    @Query("SELECT u.rol.nombre AS nombre, COUNT(u) AS total FROM Usuario u GROUP BY u.rol")
    List<Object[]> resumenPorRol();

    //Mostrar ultimos usuarios creados
    List<Usuario> findTop5ByOrderByIdDesc();

}