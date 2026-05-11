package br.com.esoft.apijogos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/")
public class ApiController {

    private final Map<Long, Jogo> jogos = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if ("usuario@esoft.com".equals(request.getEmail()) && "Abc123".equals(request.getPassword())) {
            String token = UUID.randomUUID().toString();
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/jogos")
    public ResponseEntity<List<Jogo>> listarJogos() {
        List<Jogo> lista = new ArrayList<>(jogos.values());
        lista.sort(Comparator.comparing(Jogo::getId));
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/jogos/{id}")
    public ResponseEntity<Jogo> buscarJogo(@PathVariable Long id) {
        Jogo jogo = jogos.get(id);
        if (jogo != null) {
            return ResponseEntity.ok(jogo);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/jogos")
    public ResponseEntity<Jogo> criarJogo(@RequestBody Jogo request) {
        Long newId = idCounter.incrementAndGet();
        request.setId(newId);
        jogos.put(newId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @PutMapping("/jogos/{id}")
    public ResponseEntity<Jogo> atualizarJogo(@PathVariable Long id, @RequestBody Jogo request) {
        if (jogos.containsKey(id)) {
            request.setId(id);
            jogos.put(id, request);
            return ResponseEntity.ok(request);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/jogos/{id}")
    public ResponseEntity<Void> deletarJogo(@PathVariable Long id) {
        if (jogos.remove(id) != null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}