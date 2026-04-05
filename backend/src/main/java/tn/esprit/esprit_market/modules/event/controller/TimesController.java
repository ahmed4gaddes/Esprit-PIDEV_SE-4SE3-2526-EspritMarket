package tn.esprit.esprit_market.modules.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.event.entities.Times;
import tn.esprit.esprit_market.modules.event.service.TimesService;

import java.util.List;

@RestController
@RequestMapping("/api/times")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimesController {

    private final TimesService timesService;

    // POST /api/times?liveSessionId=1
    @PostMapping
    public ResponseEntity<Times> addTimes(
            @RequestParam Long liveSessionId,
            @RequestBody Times times) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(timesService.addTimes(liveSessionId, times));
    }

    // GET /api/times
    @GetMapping
    public ResponseEntity<List<Times>> getAll() {
        return ResponseEntity.ok(timesService.getAll());
    }

    // GET /api/times/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Times> getById(@PathVariable Long id) {
        return ResponseEntity.ok(timesService.getById(id));
    }

    // GET /api/times/live-session/{liveSessionId}
    @GetMapping("/live-session/{liveSessionId}")
    public ResponseEntity<List<Times>> getByLiveSession(@PathVariable Long liveSessionId) {
        return ResponseEntity.ok(timesService.getTimesByLiveSession(liveSessionId));
    }

    // PUT /api/times/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Times> update(@PathVariable Long id, @RequestBody Times times) {
        return ResponseEntity.ok(timesService.update(id, times));
    }

    // DELETE /api/times/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
