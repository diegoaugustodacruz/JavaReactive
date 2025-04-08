package br.com.alura.codechealla.evento;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }


    public Flux<EventoDTO> obterTodos (){
        return repository.findAll().map(EventoDTO::toDTO);
    }

    public Mono<EventoDTO> obterPorId(Long id) {

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(EventoDTO::toDTO);

    }

    public Mono<EventoDTO> cadastrar(EventoDTO dto) {

        return repository.save(dto.toEntity()).map(EventoDTO::toDTO);

    }

    public Mono<Void> excluir(Long id) {

        return repository.findById(id)
                .flatMap(repository::delete);

    }

    public Mono<EventoDTO> alterar(Long id, EventoDTO dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Id do evento não encontrado.")))
                .flatMap(eventoExistente -> {
                    eventoExistente.setTipo(dto.tipo());
                    eventoExistente.setNome(dto.nome());
                    eventoExistente.setData(dto.data());
                    eventoExistente.setDescricao(dto.descricao());
                    return repository.save(eventoExistente);
                })
                .map(EventoDTO::toDTO);
    }

    public Flux<EventoDTO> obterPorTipo(String tipo) {

        TipoEvento tipoEvento = TipoEvento.valueOf(tipo.toUpperCase());

        return repository.findByTipo(tipoEvento)
                .map(EventoDTO::toDTO);

    }
}
