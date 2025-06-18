package br.com.memelandia.usuario_service.repositories;

import br.com.memelandia.usuario_service.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author rramirez
 */

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByNome(String nome);
}
