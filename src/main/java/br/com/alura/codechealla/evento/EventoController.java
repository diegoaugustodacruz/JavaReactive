package br.com.alura.codechealla.evento;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService service;
    private final Sinks.Many<EventoDTO> eventoSink;

    public EventoController(EventoService service) {
        this.service = service;
        this.eventoSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @GetMapping //(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EventoDTO> obterTodos(){
        return service.obterTodos();
    }

    @GetMapping ( value = "/categoria/{tipo}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EventoDTO> obterPorTipo(@PathVariable String tipo){
        return Flux.merge(service.obterPorTipo(tipo), eventoSink.asFlux())
                .delayElements(Duration.ofSeconds(3));
    }

    @GetMapping("/{id}")
    public Mono<EventoDTO> obterPorId(@PathVariable Long id){
        return service.obterPorId(id);
    }

    @PostMapping()
    public Mono<EventoDTO> cadastrar(@RequestBody EventoDTO dto){
        return service.cadastrar(dto)
                .doOnSuccess(e -> eventoSink.tryEmitNext(e));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> excluir(@PathVariable Long id){
        return service.excluir(id);
    }

    @PutMapping("/{id}")
    public Mono<EventoDTO> alterar(@PathVariable Long id, @RequestBody EventoDTO dto){
        return service.alterar(id, dto);
    }

}
