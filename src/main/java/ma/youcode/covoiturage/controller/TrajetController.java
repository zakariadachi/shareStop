
package ma.youcode.covoiturage.controller;

import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.TrajetRequestDTO;
import ma.youcode.covoiturage.dto.TrajetResponseDTO;
import ma.youcode.covoiturage.service.TrajetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trajets")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    public ResponseEntity<TrajetResponseDTO> creerTrajet(
            @Valid @RequestBody TrajetRequestDTO dto
    ) {
        TrajetResponseDTO response = trajetService.creerTrajet(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponseDTO> trouverParId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(trajetService.trouverParId(id));
    }

    @GetMapping
    public ResponseEntity<List<TrajetResponseDTO>> trouverTous() {
        return ResponseEntity.ok(trajetService.trouverTous());
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<Void> terminerTrajet(
            @PathVariable Long id,
            @RequestParam Long conducteurId
    ) {
        trajetService.terminerTrajet(id, conducteurId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerTrajet(
            @PathVariable Long id,
            @RequestParam Long conducteurId
    ) {
        trajetService.annulerTrajet(id, conducteurId);
        return ResponseEntity.noContent().build();
    }
}
