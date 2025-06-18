package br.com.memelandia.categoria_service.respositories;

import br.com.memelandia.categoria_service.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author rramirez
 */

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    Optional<Categoria> findByNome (String nome);
}
