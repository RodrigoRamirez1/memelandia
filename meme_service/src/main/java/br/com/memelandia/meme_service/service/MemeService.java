package br.com.memelandia.meme_service.service;

import br.com.memelandia.meme_service.domain.Meme;
import br.com.memelandia.meme_service.dto.MemeDTO;
import br.com.memelandia.meme_service.repositories.MemeRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MemeService {

    private static final Logger logger = LoggerFactory.getLogger(MemeService.class);

    private final MeterRegistry meterRegistry;
    private final MemeRepository memeRepository;
    private final RestTemplate restTemplate;

    private static final String URL_USUARIO_SERVICE = "http://localhost:8080/usuario_service/";
    private static final String URL_CATEGORIA_SERVICE = "http://localhost:8081/categoria_service/";

    public MemeService(MeterRegistry meterRegistry, MemeRepository memeRepository) {
        this.meterRegistry = meterRegistry;
        this.memeRepository = memeRepository;
        this.restTemplate = new RestTemplate();
    }
    public Optional<Meme> criarMeme(MemeDTO dto) {
        logger.info("Recebida requisição para criar um novo meme.");
        meterRegistry.counter("meme.criar.chamadas").increment();

        long start = System.currentTimeMillis();

        try {
            restTemplate.getForObject(URL_CATEGORIA_SERVICE + "nome/" + dto.getCategoriaNome(), Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Categoria '{}' não encontrada.", dto.getCategoriaNome());
            meterRegistry.counter("meme.criar.categoria.naoencontrada").increment();
            throw new RuntimeException("Categoria não encontrada: " + dto.getCategoriaNome());
        }

        try {
            restTemplate.getForObject(URL_USUARIO_SERVICE + "nome/" + dto.getUsuarioNome(), Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Usuário '{}' não encontrado.", dto.getUsuarioNome());
            meterRegistry.counter("meme.criar.usuario.naoencontrado").increment();
            throw new RuntimeException("Usuário não encontrado: " + dto.getUsuarioNome());
        }

        Meme meme = new Meme();
        meme.setNome(dto.getNome());
        meme.setDescricao(dto.getDescricao());
        meme.setUrl(dto.getUrl());
        meme.setUsuarioNome(dto.getUsuarioNome());
        meme.setCategoriaNome(dto.getCategoriaNome());
        meme.setDataCadastro(LocalDate.now());
        Meme salvo = memeRepository.save(meme);

        logger.info("Meme criado com sucesso: {}", salvo);
        meterRegistry.counter("meme.criar.sucesso").increment();
        meterRegistry.timer("meme.criar.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);

        return Optional.of(salvo);
    }

    public List<Meme> listarTodosMemes() {
        logger.info("Recebida requisição para listar todos os memes.");
        meterRegistry.counter("meme.listar.todas.chamadas").increment();

        long start = System.currentTimeMillis();
        List<Meme> memes = memeRepository.findAll();
        meterRegistry.timer("meme.listar.todas.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);

        if (memes.isEmpty()) {
            logger.warn("A lista de memes está vazia.");
            meterRegistry.counter("meme.listar.todas.vazio").increment();
        } else {
            logger.info("Total de memes encontrados: {}", memes.size());
            meterRegistry.counter("meme.listar.todas.sucesso").increment();
            meterRegistry.gauge("meme.listar.todas.quantidade", memes, List::size);
        }

        return memes;
    }

    public Optional<Meme> buscarMemePorId(UUID id) {
        logger.info("Recebida requisição para buscar meme com ID: {}", id);
        meterRegistry.counter("meme.buscar.id.chamadas").increment();

        long start = System.currentTimeMillis();
        Optional<Meme> meme = memeRepository.findById(id);
        meterRegistry.timer("meme.buscar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);

        if (meme.isPresent()) {
            logger.info("Meme encontrado: {}", meme.get());
            meterRegistry.counter("meme.buscar.id.sucesso").increment();
        } else {
            logger.warn("Meme com ID {} não encontrado.", id);
            meterRegistry.counter("meme.buscar.id.naoencontrada").increment();
        }

        return meme;
    }

    public Optional<Meme> buscarMemePorNome(String nome) {
        logger.info("Recebida requisição para buscar meme com nome: {}", nome);
        meterRegistry.counter("meme.buscar.nome.chamadas").increment();

        long start = System.currentTimeMillis();
        Optional<Meme> meme = memeRepository.findByNome(nome);
        meterRegistry.timer("meme.buscar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);

        if (meme.isPresent()) {
            logger.info("Meme encontrado: {}", meme.get());
            meterRegistry.counter("meme.buscar.nome.sucesso").increment();
        } else {
            logger.warn("Meme com nome {} não encontrado.", nome);
            meterRegistry.counter("meme.buscar.nome.naoencontrada").increment();
        }

        return meme;
    }

    public boolean deletarMemePorId(UUID id) {
        logger.info("Recebida requisição para deletar meme com ID: {}", id);
        meterRegistry.counter("meme.deletar.id.chamadas").increment();

        long start = System.currentTimeMillis();

        Optional<Meme> memeExistente = memeRepository.findById(id);
        if (memeExistente.isPresent()) {
            memeRepository.deleteById(id);
            logger.info("Meme com ID {} deletado com sucesso.", id);
            meterRegistry.counter("meme.deletar.id.sucesso").increment();
            meterRegistry.timer("meme.deletar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return true;
        } else {
            logger.warn("Meme com ID {} não encontrado.", id);
            meterRegistry.counter("meme.deletar.id.naoencontrada").increment();
            meterRegistry.timer("meme.deletar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return false;
        }
    }

    public boolean deletarMemePorNome(String nome) {
        logger.info("Recebida requisição para deletar meme com nome: {}", nome);
        meterRegistry.counter("meme.deletar.nome.chamadas").increment();

        long start = System.currentTimeMillis();

        Optional<Meme> memeExistente = memeRepository.findByNome(nome);
        if (memeExistente.isPresent()) {
            memeRepository.delete(memeExistente.get());
            logger.info("Meme com nome {} deletado com sucesso.", nome);
            meterRegistry.counter("meme.deletar.nome.sucesso").increment();
            meterRegistry.timer("meme.deletar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return true;
        } else {
            logger.warn("Meme com nome {} não encontrado.", nome);
            meterRegistry.counter("meme.deletar.nome.naoencontrada").increment();
            meterRegistry.timer("meme.deletar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return false;
        }
    }


    public Meme obterMemeDoDia() {
        logger.info("Selecionando meme do dia.");
        return Optional.ofNullable(memeRepository.findRandomMeme())
                .orElseThrow(() -> new RuntimeException("Nenhum meme encontrado no banco de dados."));
    }

}
