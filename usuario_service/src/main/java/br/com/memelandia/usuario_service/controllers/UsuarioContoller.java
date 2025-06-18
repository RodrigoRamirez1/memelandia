package br.com.memelandia.usuario_service.controllers;

import br.com.memelandia.usuario_service.domain.Usuario;
import br.com.memelandia.usuario_service.dto.UsuarioDTO;
import br.com.memelandia.usuario_service.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Usuários", description = "Endpoints referente a usuários")
@RestController
@RequestMapping("/usuario_service")
public class UsuarioContoller {
    private final UsuarioService usuarioService;

    public UsuarioContoller(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Criar", description = "Método para criar um novo usuário")
    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody UsuarioDTO dto) {
        return usuarioService.criarUsuario(dto)
                .<ResponseEntity<?>>map(novoUsuario -> ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário com este nome já existe."));
    }

    @Operation(summary = "Listar", description = "Método para listar odos os usuários")
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodosUsuarios(){
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Buscar Por Id", description = "Método para buscar um usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable UUID id) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Buscar Por Nome", description = "Método para buscar um usuário por Nome")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Usuario> buscarUsuarioPorNome(@PathVariable String nome) {
        return usuarioService.buscarUsuarioPorNome(nome)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Deletar Por Id", description = "Método para deletar um usuário por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuarioPorId(@PathVariable UUID id) {
        boolean removido = usuarioService.deletarUsuarioPorId(id);
        if (removido) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado para exclusão.");
        }
    }

    @Operation(summary = "Deletar Por Nome", description = "Método para deletar um usuário por Nome")
    @DeleteMapping("/nome/{nome}")
    public ResponseEntity<?> deletarUsuarioPorNome(@PathVariable String nome) {
        boolean removido = usuarioService.deletarUsuarioPorNome(nome);
        if (removido) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado para exclusão.");
        }
    }




}
