package br.com.memelandia.categoria_service.service;

import br.com.memelandia.categoria_service.domain.Categoria;
import br.com.memelandia.categoria_service.dto.CategoriaDTO;
import br.com.memelandia.categoria_service.respositories.CategoriaRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.cloud.stream.function.StreamBridge;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CategoriaService {
    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    private final StreamBridge streamBridge;
    private final CategoriaRepository categoriaRepository;
    private final MeterRegistry meterRegistry;

    public CategoriaService(StreamBridge streamBridge, CategoriaRepository categoriaRepository, MeterRegistry meterRegistry) {
        this.streamBridge = streamBridge;
        this.categoriaRepository = categoriaRepository;
        this.meterRegistry = meterRegistry;
    }



    public List<Categoria> listarTodasCategorias() {
        logger.info("Recebida requisição para listar todas as categorias.");
        meterRegistry.counter("categoria.listar.todas.chamadas").increment();

        long start = System.currentTimeMillis();
        List<Categoria> categorias = categoriaRepository.findAll();
        long end = System.currentTimeMillis();
        meterRegistry.timer("categoria.listar.todas.tempo").record(end - start, TimeUnit.MILLISECONDS);

        if (categorias.isEmpty()) {
            logger.warn("Lista de categorias retornou vazia.");
            meterRegistry.counter("categoria.listar.todas.vazio").increment();
        } else {
            logger.info("Total de categorias encontradas: {}", categorias.size());
            meterRegistry.counter("categoria.listar.todas.sucesso").increment();
            meterRegistry.gauge("categoria.listar.todas.quantidade", categorias, List::size);
        }

        return categorias;
    }

    public Optional<Categoria> criarCategoria(CategoriaDTO dto) {
        logger.info("Recebida requisição para criar nova categoria: {}", dto);
        meterRegistry.counter("categoria.criar.chamadas").increment();

        long start = System.currentTimeMillis();

        Optional<Categoria> existente = categoriaRepository.findByNome(dto.getNome());
        if (existente.isPresent()) {
            logger.warn("Categoria com nome '{}' já existe.", dto.getNome());
            meterRegistry.counter("categoria.criar.existente").increment();
            return Optional.empty();
        }

        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setDataCadastro(LocalDate.now());
        Categoria salva = categoriaRepository.save(categoria);
        meterRegistry.counter("categoria.criar.sucesso").increment();
        logger.info("Categoria criada com sucesso: {}", salva);

        try {
            streamBridge.send("categoriaEventos-out-0", salva);
            logger.info("Evento de criação de categoria enviado com sucesso via StreamBridge.");
        } catch (Exception e) {
            logger.error("Erro ao enviar evento de criação via StreamBridge: {}", e.getMessage());
            meterRegistry.counter("categoria.criar.evento.falha").increment();
        }

        long end = System.currentTimeMillis();
        meterRegistry.timer("categoria.criar.tempo").record(end - start, TimeUnit.MILLISECONDS);

        return Optional.of(salva);
    }



    public Optional<Categoria> buscarCategoriaPorID(UUID id) {
        logger.info("Recebida requisição para buscar categoria com ID: {}", id);
        meterRegistry.counter("categoria.buscar.id.chamadas").increment();

        long start = System.currentTimeMillis();
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        long end = System.currentTimeMillis();
        meterRegistry.timer("categoria.buscar.id.tempo").record(end - start, TimeUnit.MILLISECONDS);

        if (categoria.isPresent()) {
            logger.info("Categoria encontrada: {}", categoria.get());
            meterRegistry.counter("categoria.buscar.id.sucesso").increment();
        } else {
            logger.warn("Categoria com ID {} não encontrada.", id);
            meterRegistry.counter("categoria.buscar.id.naoencontrada").increment();
        }

        return categoria;
    }


    public Optional<Categoria> buscarCategoriaPorNome(String nome) {
        logger.info("Recebida requisição para buscar categoria com Nome: {}", nome);
        meterRegistry.counter("categoria.buscar.nome.chamadas").increment();

        long start = System.currentTimeMillis();
        Optional<Categoria> categoria = categoriaRepository.findByNome(nome);
        long end = System.currentTimeMillis();
        meterRegistry.timer("categoria.buscar.nome.tempo").record(end - start, TimeUnit.MILLISECONDS);

        if (categoria.isPresent()) {
            logger.info("Categoria encontrada: {}", categoria.get());
            meterRegistry.counter("categoria.buscar.nome.sucesso").increment();
        } else {
            logger.warn("Categoria com nome {} não encontrada.", nome);
            meterRegistry.counter("categoria.buscar.nome.naoencontrada").increment();
        }

        return categoria;
    }

    public boolean deletarCategoriaPorId(UUID id) {
        logger.info("Recebida requisição para deletar categoria com ID: {}", id);
        meterRegistry.counter("categoria.deletar.Id.chamadas").increment();

        long start = System.currentTimeMillis();

        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (categoriaExistente.isPresent()) {
            categoriaRepository.deleteById(id);
            logger.info("Categoria com ID {} deletada com sucesso.", id);
            meterRegistry.counter("categoria.deletar.id.sucesso").increment();
            meterRegistry.timer("categoria.deletar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return true;
        } else {
            logger.warn("Categoria com ID {} não encontrada para exclusão.", id);
            meterRegistry.counter("categoria.deletar.id.naoencontrada").increment();
            meterRegistry.timer("categoria.deletar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return false;
        }
    }

    public boolean deletarCategoriaPorNome(String nome) {
        logger.info("Recebida requisição para deletar categoria com nome: {}", nome);
        meterRegistry.counter("categoria.deletar.nome.chamadas").increment();

        long start = System.currentTimeMillis();

        Optional<Categoria> categoriaExistente = categoriaRepository.findByNome(nome);
        if (categoriaExistente.isPresent()) {
            categoriaRepository.delete(categoriaExistente.get());
            logger.info("Categoria com nome {} deletada com sucesso.", nome);
            meterRegistry.counter("categoria.deletar.nome.sucesso").increment();
            meterRegistry.timer("categoria.deletar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return true;
        } else {
            logger.warn("Categoria com nome {} não encontrada para exclusão.", nome);
            meterRegistry.counter("categoria.deletar.nome.naoencontrada").increment();
            meterRegistry.timer("categoria.deletar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return false;
        }
    }
}
