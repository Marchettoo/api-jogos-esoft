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
    public ResponseEntity<LoginResponse> login(@RequestBody(required = false) LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
    public ResponseEntity<Jogo> criarJogo(@RequestBody(required = false) Jogo request) {
        if (!isJogoValido(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Long newId = idCounter.incrementAndGet();
        request.setId(newId);
        jogos.put(newId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @PutMapping("/jogos/{id}")
    public ResponseEntity<Jogo> atualizarJogo(@PathVariable Long id, @RequestBody(required = false) Jogo request) {
        if (!isJogoValido(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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

    private boolean isJogoValido(Jogo jogo) {
        if (jogo == null) return false;
        if (jogo.getNome() == null || jogo.getNome().trim().isEmpty()) return false;
        if (jogo.getTipo() == null || jogo.getTipo().trim().isEmpty()) return false;
        if (jogo.getNota() == null) return false;
        if (jogo.getReview() == null || jogo.getReview().trim().isEmpty()) return false;
        return true;
    }
}