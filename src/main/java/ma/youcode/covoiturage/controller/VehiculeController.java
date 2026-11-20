package ma.youcode.covoiturage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.VehiculeRequestDTO;
import ma.youcode.covoiturage.dto.VehiculeResponseDTO;
import ma.youcode.covoiturage.exception.ErrorResponse;
import ma.youcode.covoiturage.service.VehiculeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
@Tag(name = "Véhicules", description = "Gestion des véhicules : création, consultation, modification et suppression")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @Operation(summary = "Créer un véhicule", description = "Enregistre un nouveau véhicule rattaché à un conducteur existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès",
                    content = @Content(schema = @Schema(implementation = VehiculeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (champs obligatoires manquants ou capacité hors limites [1-8])",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conducteur introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflit — immatriculation déjà existante",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeResponseDTO creer(@Valid @RequestBody VehiculeRequestDTO dto) {
        return vehiculeService.creerVehicule(dto);
    }

    @Operation(summary = "Trouver un véhicule par ID", description = "Retourne les détails d'un véhicule à partir de son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé",
                    content = @Content(schema = @Schema(implementation = VehiculeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public VehiculeResponseDTO trouverParId(
            @Parameter(description = "Identifiant du véhicule", example = "1")
            @PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @Operation(summary = "Lister les véhicules d'un conducteur", description = "Retourne la liste de tous les véhicules appartenant à un conducteur donné.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des véhicules du conducteur retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Conducteur introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/conducteur/{conducteurId}")
    public List<VehiculeResponseDTO> trouverParConducteur(
            @Parameter(description = "Identifiant du conducteur", example = "1")
            @PathVariable Long conducteurId) {
        return vehiculeService.findByConducteurId(conducteurId);
    }

    @Operation(summary = "Modifier un véhicule", description = "Met à jour les informations d'un véhicule existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié avec succès",
                    content = @Content(schema = @Schema(implementation = VehiculeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Véhicule ou conducteur introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflit d'intégrité",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public VehiculeResponseDTO modifier(
            @Parameter(description = "Identifiant du véhicule à modifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody VehiculeRequestDTO dto
    ) {
        return vehiculeService.modifierVehicule(id, dto);
    }

    @Operation(summary = "Supprimer un véhicule", description = "Supprime un véhicule existant s'il n'est associé à aucun trajet.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Règle métier enfreinte — impossible de supprimer un véhicule associé à un ou plusieurs trajets",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(
            @Parameter(description = "Identifiant du véhicule à supprimer", example = "1")
            @PathVariable Long id) {
        vehiculeService.supprimerVehicule(id);
    }
}
