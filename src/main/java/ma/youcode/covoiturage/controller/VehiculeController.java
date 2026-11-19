
package ma.youcode.covoiturage.controller;

import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.VehiculeRequestDTO;
import ma.youcode.covoiturage.dto.VehiculeResponseDTO;
import ma.youcode.covoiturage.service.VehiculeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeResponseDTO creer(@Valid @RequestBody VehiculeRequestDTO dto) {
        return vehiculeService.creerVehicule(dto);
    }

    @GetMapping
    public List<VehiculeResponseDTO> trouverTous() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    public VehiculeResponseDTO trouverParId(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PutMapping("/{id}")
    public VehiculeResponseDTO modifier(
            @PathVariable Long id,
            @Valid @RequestBody VehiculeRequestDTO dto
    ) {
        return vehiculeService.modifierVehicule(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        vehiculeService.supprimerVehicule(id);
    }
}
