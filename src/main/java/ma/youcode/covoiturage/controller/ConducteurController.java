package ma.youcode.covoiturage.controller;


import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;
import ma.youcode.covoiturage.service.ConducteurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conducteurs")
public class ConducteurController  {

    private final ConducteurService conducteurService;

    public ConducteurController(ConducteurService conducteurService){
        this.conducteurService = conducteurService;
    }

    @PostMapping
    public ConducteurResponseDTO creerConducteur(@Valid @RequestBody ConducteurRequestDTO dto){
        return conducteurService.creerConducteur(dto);
    }

    @GetMapping("/{id}")
    public ConducteurResponseDTO trouverParId(@PathVariable Long id){
        return conducteurService.findById(id);
    }

    @GetMapping
    public List<ConducteurResponseDTO> trouverTous() {
        return conducteurService.findAll();
    }

    @PutMapping("/{id}")
    public ConducteurResponseDTO modifier(
            @PathVariable Long id,
            @Valid @RequestBody ConducteurRequestDTO dto
    ) {
        return conducteurService.modifierConducteur(id, dto);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id) {
        conducteurService.supprimerConducteur(id);
    }

}
