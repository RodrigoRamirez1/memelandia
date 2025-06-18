package br.com.memelandia.categoria_service.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * Representa uma categoria do sistema.
 * Armazena informações de descrição e data de cadastro.
 *
 * @author rramirez
 */

@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotBlank(message = "Nome da Categória é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank(message = "Descrição é obrigatório")
    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "data_categoria", nullable = false)
    private LocalDate dataCadastro;

    public Categoria(){}

    public Categoria(UUID id, String nome, String descricao, LocalDate dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCadastro = dataCadastro;
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

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
