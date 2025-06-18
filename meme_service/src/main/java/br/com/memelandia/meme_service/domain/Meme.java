package br.com.memelandia.meme_service.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * Representa um meme do sistema.
 * Armazena informações de descrição e data de cadastro.
 *
 * @author rramirez
 */

@Entity
@Table(name = "meme")
public class Meme {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotBlank(message = "Nome do meme é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank(message = "Descrição do meme é obrigatório")
    @Column(name = "descricao", nullable = false)
    private String descricao;

    @NotBlank(message = "Url do meme é obrigatório")
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @NotBlank(message = "Nome da categoria do meme é obrigatório")
    @Column(name = "categoria_nome", nullable = false)
    private String  categoriaNome;

    @NotBlank(message = "Nome do usuário do meme é obrigatório")
    @Column(name = "usuario_nome", nullable = false)
    private String usuarioNome;

    public Meme(){
    }
    public Meme(UUID id, String nome, String descricao, String url, LocalDate dataCadastro, String categoriaNome, String usuarioNome) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.url = url;
        this.dataCadastro = dataCadastro;
        this.categoriaNome = categoriaNome;
        this.usuarioNome = usuarioNome;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getCategoriaNome() {
        return categoriaNome;
    }

    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }
}
