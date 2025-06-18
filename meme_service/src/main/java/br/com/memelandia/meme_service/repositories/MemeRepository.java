package br.com.memelandia.meme_service.repositories;

import br.com.memelandia.meme_service.domain.Meme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

/**
 * @author rramirez
 */

public interface MemeRepository extends JpaRepository<Meme, UUID> {

        @Query(value = "SELECT * FROM Meme ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
        Meme findRandomMeme();

        Optional<Meme> findByNome(String nome);
}
