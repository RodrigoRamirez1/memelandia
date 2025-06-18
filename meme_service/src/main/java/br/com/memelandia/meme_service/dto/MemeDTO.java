package br.com.memelandia.meme_service.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author rramirez
 */

public class MemeDTO {
    @NotBlank(message = "Nome do meme é obrigatório")
    private String nome;
    @NotBlank(message = "Descriçao do meme é obrigatório")
    private String descricao;
    @NotBlank(message = "Url do meme é obrigatório")
    private String url;
    @NotBlank(message = "Nome da categoria do meme é obrigatório")
    private String categoriaNome;
    @NotBlank(message = "Nome do usuário do meme é obrigatório")
    private String usuarioNome;

    public MemeDTO(){}
    public MemeDTO(String nome, String descricao, String url, String categoriaNome, String usuarioNome) {
        this.nome = nome;
        this.descricao = descricao;
        this.url = url;
        this.categoriaNome = categoriaNome;
        this.usuarioNome = usuarioNome;
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
