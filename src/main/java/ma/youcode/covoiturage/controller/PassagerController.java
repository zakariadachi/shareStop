package ma.youcode.covoiturage.controller;

import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.PassagerRequestDTO;
import ma.youcode.covoiturage.dto.PassagerResponseDTO;
import ma.youcode.covoiturage.service.PassagerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passagers")
public class PassagerController {

    private final PassagerService passagerService;

    public PassagerController(PassagerService passagerService) {
        this.passagerService = passagerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassagerResponseDTO creer(@Valid @RequestBody PassagerRequestDTO dto) {
        return passagerService.creerPassager(dto);
    }

    @GetMapping
    public List<PassagerResponseDTO> trouverTous() {
        return passagerService.findAll();
    }

    @GetMapping("/{id}")
    public PassagerResponseDTO trouverParId(@PathVariable Long id) {
        return passagerService.findById(id);
    }

    @GetMapping("/email/{email}")
    public PassagerResponseDTO trouverParEmail(@PathVariable String email) {
        return passagerService.findByEmail(email);
    }

    @PutMapping("/{id}")
    public PassagerResponseDTO modifier(
            @PathVariable Long id,
            @Valid @RequestBody PassagerRequestDTO dto
    ) {
        return passagerService.modifierPassager(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        passagerService.supprimerPassager(id);
    }
}