package ma.youcode.covoiturage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.PassagerRequestDTO;
import ma.youcode.covoiturage.dto.PassagerResponseDTO;
import ma.youcode.covoiturage.exception.ErrorResponse;
import ma.youcode.covoiturage.service.PassagerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passagers")
@Tag(name = "Passagers", description = "Gestion des passagers : création, consultation, recherche par email et modification")
public class PassagerController {

    private final PassagerService passagerService;

    public PassagerController(PassagerService passagerService) {
        this.passagerService = passagerService;
    }

    @Operation(summary = "Créer un passager", description = "Enregistre un nouveau passager avec ses informations personnelles.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Passager créé avec succès",
                    content = @Content(schema = @Schema(implementation = PassagerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (champs obligatoires manquants ou format incorrect)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflit — un passager avec le même email existe déjà",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassagerResponseDTO creer(@Valid @RequestBody PassagerRequestDTO dto) {
        return passagerService.creerPassager(dto);
    }

    @Operation(summary = "Lister tous les passagers", description = "Retourne la liste complète de tous les passagers enregistrés.")
    @ApiResponse(responseCode = "200", description = "Liste des passagers retournée avec succès")
    @GetMapping
    public List<PassagerResponseDTO> trouverTous() {
        return passagerService.findAll();
    }

    @Operation(summary = "Trouver un passager par ID", description = "Retourne les détails d'un passager à partir de son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passager trouvé",
                    content = @Content(schema = @Schema(implementation = PassagerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Passager non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public PassagerResponseDTO trouverParId(
            @Parameter(description = "Identifiant du passager", example = "1")
            @PathVariable Long id) {
        return passagerService.findById(id);
    }

    @Operation(summary = "Trouver un passager par email", description = "Retourne les détails d'un passager à partir de son adresse email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passager trouvé",
                    content = @Content(schema = @Schema(implementation = PassagerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Aucun passager trouvé avec cet email",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/email/{email}")
    public PassagerResponseDTO trouverParEmail(
            @Parameter(description = "Adresse email du passager", example = "ahmed@email.com")
            @PathVariable String email) {
        return passagerService.findByEmail(email);
    }

    @Operation(summary = "Modifier un passager", description = "Met à jour les informations d'un passager existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passager modifié avec succès",
                    content = @Content(schema = @Schema(implementation = PassagerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Passager non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflit — email déjà utilisé par un autre passager",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public PassagerResponseDTO modifier(
            @Parameter(description = "Identifiant du passager à modifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PassagerRequestDTO dto
    ) {
        return passagerService.modifierPassager(id, dto);
    }
}
