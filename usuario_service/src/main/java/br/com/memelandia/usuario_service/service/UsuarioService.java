package br.com.memelandia.usuario_service.service;

import br.com.memelandia.usuario_service.domain.Usuario;
import br.com.memelandia.usuario_service.dto.UsuarioDTO;
import br.com.memelandia.usuario_service.repositories.UsuarioRepository;
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
public class UsuarioService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final StreamBridge streamBridge;
    private final UsuarioRepository usuarioRepository;
    private final MeterRegistry meterRegistry;

    public UsuarioService(StreamBridge streamBridge, UsuarioRepository usuarioRepository, MeterRegistry meterRegistry) {
        this.streamBridge = streamBridge;
        this.usuarioRepository = usuarioRepository;
        this.meterRegistry = meterRegistry;
    }

    public Optional<Usuario> criarUsuario(UsuarioDTO dto) {
        logger.info("Recebido requisição para criar novo usuário: {}", dto);
        meterRegistry.counter("usuario.criar.chamadas").increment();

        long start = System.currentTimeMillis();

        Optional<Usuario> usuarioExiste = usuarioRepository.findByNome(dto.getNome());
        if (usuarioExiste.isPresent()) {
            logger.warn("Usuário com nome '{}' já existe. Encerrando a ação.", dto.getNome());
            meterRegistry.counter("usuario.criar.duplicado").increment();
            return Optional.empty();
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setDataCadastro(LocalDate.now());
        Usuario salvo = usuarioRepository.save(usuario);


        meterRegistry.counter("usuario.criar.sucesso").increment();
        logger.info("Usuário criado com sucesso: {}", salvo);

        try {
            streamBridge.send("usuarioEventos-out-0", salvo);
            logger.info("Evento de criação enviado com sucesso via StreamBridge.");
        } catch (Exception e) {
            logger.error("Erro ao enviar evento de criação via StreamBridge: {}", e.getMessage());
            meterRegistry.counter("usuario.criar.evento.falha").increment();
        }

        long end = System.currentTimeMillis();
        meterRegistry.timer("usuario.criar.tempo").record(end - start, TimeUnit.MILLISECONDS);

        return Optional.of(salvo);
    }

    public List<Usuario> listarTodosUsuarios() {
        logger.info("Recebida requisição para listar todos os usuários.");
        meterRegistry.counter("usuario.listar.todas.chamadas").increment();

        long start = System.currentTimeMillis();
        List<Usuario> usuarios = usuarioRepository.findAll();
        long end = System.currentTimeMillis();
        meterRegistry.timer("usuario.listar.todas.tempo").record(end - start, TimeUnit.MILLISECONDS);

        if (usuarios.isEmpty()) {
            logger.warn("Lista de usuários retornou vazia.");
            meterRegistry.counter("usuario.listar.todas.vazio").increment();
        } else {
            logger.info("Total de usuários encontrados: {}", usuarios.size());
            meterRegistry.counter("usuario.listar.todas.sucesso").increment();
            meterRegistry.gauge("usuario.listar.todas.quantidade", usuarios, List::size);
        }

        return usuarios;
    }

    public Optional<Usuario> buscarUsuarioPorId(UUID id) {
        logger.info("Recebida requisição para buscar usuário com ID: {}", id);
        meterRegistry.counter("usuario.buscar.id.chamadas").increment();

        long start = System.currentTimeMillis();
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        long end = System.currentTimeMillis();
        meterRegistry.timer("usuario.buscar.id.tempo").record(end - start, TimeUnit.MILLISECONDS);

        if (usuario.isPresent()) {
            logger.info("Usuário encontrado: {}", usuario.get());
            meterRegistry.counter("usuario.buscar.id.sucesso").increment();
        } else {
            logger.warn("Usuário com ID {} não encontrado.", id);
            meterRegistry.counter("usuario.buscar.id.naoencontrado").increment();
        }

        return usuario;
    }

    public Optional<Usuario> buscarUsuarioPorNome(String nome) {
        logger.info("Recebida requisição para buscar usuário com Nome: {}", nome);
        meterRegistry.counter("usuario.buscar.nome.chamadas").increment();

        long start = System.currentTimeMillis();
        Optional<Usuario> usuario = usuarioRepository.findByNome(nome);
        long end = System.currentTimeMillis();
        meterRegistry.timer("usuario.buscar.nome.tempo").record(end - start, TimeUnit.MILLISECONDS);

        if (usuario.isPresent()) {
            logger.info("Usuário encontrado: {}", usuario.get());
            meterRegistry.counter("usuario.buscar.nome.sucesso").increment();
        } else {
            logger.warn("Usuário com nome {} não encontrado.", nome);
            meterRegistry.counter("usuario.buscar.nome.naoencontrado").increment();
        }

        return usuario;
    }

    public boolean deletarUsuarioPorId(UUID id) {
        logger.info("Recebida requisição para deletar usuário com ID: {}", id);
        meterRegistry.counter("usuario.deletar.id.chamadas").increment();

        long start = System.currentTimeMillis();

        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            usuarioRepository.deleteById(id);
            logger.info("Usuário com ID {} deletado com sucesso.", id);
            meterRegistry.counter("usuario.deletar.id.sucesso").increment();
            meterRegistry.timer("usuario.deletar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return true;
        } else {
            logger.warn("Usuário com ID {} não encontrado para exclusão.", id);
            meterRegistry.counter("usuario.deletar.id.naoencontrado").increment();
            meterRegistry.timer("usuario.deletar.id.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return false;
        }
    }

    public boolean deletarUsuarioPorNome(String nome) {
        logger.info("Recebida requisição para deletar usuário com nome: {}", nome);
        meterRegistry.counter("usuario.deletar.nome.chamadas").increment();

        long start = System.currentTimeMillis();

        Optional<Usuario> usuarioExistente = usuarioRepository.findByNome(nome);
        if (usuarioExistente.isPresent()) {
            usuarioRepository.delete(usuarioExistente.get());
            logger.info("Usuário com nome {} deletado com sucesso.", nome);
            meterRegistry.counter("usuario.deletar.nome.sucesso").increment();
        } else {
            logger.warn("Usuário com nome {} não encontrado para exclusão.", nome);
            meterRegistry.counter("usuario.deletar.nome.naoencontrado").increment();
            meterRegistry.timer("usuario.deletar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return false;
        }

        meterRegistry.timer("usuario.deletar.nome.tempo").record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
        return true;
    }


}
