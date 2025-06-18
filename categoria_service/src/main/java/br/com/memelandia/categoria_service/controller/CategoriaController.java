package br.com.memelandia.categoria_service.controller;

import br.com.memelandia.categoria_service.domain.Categoria;
import br.com.memelandia.categoria_service.dto.CategoriaDTO;
import br.com.memelandia.categoria_service.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author rramirez
 */

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Listar", description = "Listar todas as categorias")
    @GetMapping
    public ResponseEntity<List<Categoria>> ListarTodaCategoria(){
        return ResponseEntity.ok(categoriaService.listarTodasCategorias());
    }

    @Operation(summary = "Criar", description = "Criar categoria de meme")
    @PostMapping
    public ResponseEntity<?> criarCategoria(@RequestBody CategoriaDTO categoriaDTO){
        Optional<Categoria> novoCategoria = categoriaService.criarCategoria(categoriaDTO);
        if (novoCategoria.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCategoria.get());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Categoria com este nome já criada");
        }
    }

    @Operation(summary = "Buscar por Id", description = "Buscar categoria por Id")
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoriaPorId(@PathVariable UUID id){
        Optional<Categoria> categoria = categoriaService.buscarCategoriaPorID(id);

        if(categoria.isPresent()){
            return ResponseEntity.ok(categoria.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar por Nome", description = "Buscar categoria por nome")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Categoria> buscarCategoriaPorNome(@PathVariable String nome){
        Optional<Categoria> categoria = categoriaService.buscarCategoriaPorNome(nome);

        if(categoria.isPresent()){
            return ResponseEntity.ok(categoria.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar por Id", description = "Deletar categoria por Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCategoriaPorId(@PathVariable UUID id){
        boolean removido = categoriaService.deletarCategoriaPorId(id);
        if (removido) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoria não encontrada para exclusão.");
        }
    }

    @Operation(summary = "Deletar por Nome", description = "Deletar categoria por nome")
    @DeleteMapping("/nome/{nome}")
    public ResponseEntity<?> deletarCategoriaPorNome(@PathVariable String nome){
        boolean removido = categoriaService.deletarCategoriaPorNome(nome);
        if(removido){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoria não encontrada para exclusão");
        }
    }
}
