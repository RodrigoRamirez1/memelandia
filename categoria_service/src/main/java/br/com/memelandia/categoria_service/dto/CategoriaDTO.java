package br.com.memelandia.categoria_service.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author rramirez
 */

public class CategoriaDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    @NotBlank(message = "Descrição é obrigatório")
    private String descricao;

    public CategoriaDTO(){}

    public CategoriaDTO(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
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
}
