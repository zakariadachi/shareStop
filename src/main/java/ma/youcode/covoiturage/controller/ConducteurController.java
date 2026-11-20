package ma.youcode.covoiturage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;
import ma.youcode.covoiturage.exception.ErrorResponse;
import ma.youcode.covoiturage.service.ConducteurService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conducteurs")
@Tag(name = "Conducteurs", description = "Gestion des conducteurs : création, consultation, recherche et modification")
public class ConducteurController {

    private final ConducteurService conducteurService;

    public ConducteurController(ConducteurService conducteurService) {
        this.conducteurService = conducteurService;
    }

    @Operation(summary = "Créer un conducteur", description = "Enregistre un nouveau conducteur avec ses informations personnelles et son numéro de permis.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conducteur créé avec succès",
                    content = @Content(schema = @Schema(implementation = ConducteurResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (champs obligatoires manquants ou format incorrect)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflit — un conducteur avec le même email existe déjà",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConducteurResponseDTO creerConducteur(@Valid @RequestBody ConducteurRequestDTO dto) {
        return conducteurService.creerConducteur(dto);
    }

    @Operation(summary = "Trouver un conducteur par ID", description = "Retourne les détails d'un conducteur à partir de son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conducteur trouvé",
                    content = @Content(schema = @Schema(implementation = ConducteurResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conducteur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ConducteurResponseDTO trouverParId(
            @Parameter(description = "Identifiant du conducteur", example = "1")
            @PathVariable Long id) {
        return conducteurService.findById(id);
    }

    @Operation(summary = "Lister tous les conducteurs", description = "Retourne la liste complète de tous les conducteurs enregistrés.")
    @ApiResponse(responseCode = "200", description = "Liste des conducteurs retournée avec succès")
    @GetMapping
    public List<ConducteurResponseDTO> trouverTous() {
        return conducteurService.findAll();
    }

    @Operation(summary = "Rechercher des conducteurs", description = "Recherche des conducteurs par nom, prénom ou email contenant le terme de recherche.")
    @ApiResponse(responseCode = "200", description = "Résultats de la recherche retournés avec succès")
    @GetMapping("/recherche")
    public List<ConducteurResponseDTO> rechercher(
            @Parameter(description = "Terme de recherche (nom, prénom ou email)", example = "Mohamed")
            @RequestParam String query) {
        return conducteurService.rechercher(query);
    }

    @Operation(summary = "Modifier un conducteur", description = "Met à jour les informations d'un conducteur existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conducteur modifié avec succès",
                    content = @Content(schema = @Schema(implementation = ConducteurResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conducteur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflit — email déjà utilisé par un autre conducteur",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ConducteurResponseDTO modifier(
            @Parameter(description = "Identifiant du conducteur à modifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ConducteurRequestDTO dto
    ) {
        return conducteurService.modifierConducteur(id, dto);
    }
}
