package br.com.memelandia.meme_service.controller;

import br.com.memelandia.meme_service.domain.Meme;
import br.com.memelandia.meme_service.dto.MemeDTO;
import br.com.memelandia.meme_service.service.MemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Memes", description = "Endpoints para gerenciamento de memes")
@RestController
@RequestMapping("/meme")
public class MemeController {

    private final MemeService memeService;

    public MemeController(MemeService memeService) {
        this.memeService = memeService;
    }

    @Operation(summary = "Listar", description = "Listar todos os memes")
    @GetMapping
    public ResponseEntity<List<Meme>> listarTodosMemes() {
        return ResponseEntity.ok(memeService.listarTodosMemes());
    }

    @Operation(summary = "Criar", description = "Criar um novo meme")
    @PostMapping
    public ResponseEntity<Meme> criarMeme(@RequestBody MemeDTO dto) {
        Meme novoMeme = memeService.criarMeme(dto).orElseThrow(() -> new RuntimeException("Erro ao criar meme."));
        return ResponseEntity.status(HttpStatus.CREATED).body(novoMeme);
    }

    @Operation(summary = "Buscar Por Id", description = "Buscar um meme pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<Meme> buscarMemePorId(@PathVariable UUID id) {
        return memeService.buscarMemePorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Buscar Por Nome", description = "Buscar um meme pelo ID")
    @GetMapping("/name/{name}")
    public ResponseEntity<Meme> buscarMemePorNome(@PathVariable String name) {
        return memeService.buscarMemePorNome(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Deletar Por Id", description = "Deletar um meme pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMemePorId(@PathVariable UUID id) {
        boolean removido = memeService.deletarMemePorId(id);
        if (removido) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Meme não encontrado para exclusão.");
        }
    }

    @Operation(summary = "Deletar Por Nome", description = "Deletar um meme pelo Nome")
    @DeleteMapping("/name/{name}")
    public ResponseEntity<?> deletarMemePorNome(@PathVariable String name) {
        boolean removido = memeService.deletarMemePorNome(name);
        if (removido) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Meme não encontrado para exclusão.");
        }
    }

    @Operation(summary = "Meme do Dia", description = "Selecionar um meme aleatório do banco de dados")
    @GetMapping("/meme-do-dia")
    public ResponseEntity<Meme> obterMemeDoDia() {
        Meme meme = memeService.obterMemeDoDia();
        return ResponseEntity.ok(meme);
    }

}
