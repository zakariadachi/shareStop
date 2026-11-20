package ma.youcode.covoiturage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.TrajetRequestDTO;
import ma.youcode.covoiturage.dto.TrajetResponseDTO;
import ma.youcode.covoiturage.dto.TrajetSearchProjection;
import ma.youcode.covoiturage.dto.TrajetSearchRequestDTO;
import ma.youcode.covoiturage.exception.ErrorResponse;
import ma.youcode.covoiturage.service.TrajetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/trajets")
@Tag(name = "Trajets", description = "Gestion et recherche des trajets de covoiturage multi-étapes")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @Operation(summary = "Créer un trajet", description = "Crée un nouveau trajet multi-étapes avec au moins deux points d'arrêt ordonnés. Vérifie que le véhicule appartient au conducteur, que la capacité est respectée, que le départ est futur et que les prix sont croissants.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trajet créé avec succès",
                    content = @Content(schema = @Schema(implementation = TrajetResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (champs manquants ou format incorrect)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conducteur ou véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Règle métier enfreinte (véhicule non assigné, capacité dépassée, incohérence temporelle ou tarifaire des arrêts)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TrajetResponseDTO> creerTrajet(
            @Valid @RequestBody TrajetRequestDTO dto
    ) {
        TrajetResponseDTO response = trajetService.creerTrajet(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Trouver un trajet par ID", description = "Retourne les détails complets d'un trajet (arrêts ordonnés, conducteur, véhicule et places restantes).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trajet trouvé",
                    content = @Content(schema = @Schema(implementation = TrajetResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trajet introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponseDTO> trouverParId(
            @Parameter(description = "Identifiant du trajet", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(trajetService.trouverParId(id));
    }

    @Operation(summary = "Terminer un trajet", description = "Clôture un trajet. Seul le conducteur propriétaire peut clôturer le trajet et l'heure de départ doit être passée.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Trajet terminé avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — le conducteur n'est pas le propriétaire du trajet",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trajet introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Règle métier enfreinte (statut incompatible ou trajet non démarré)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/terminer")
    public ResponseEntity<Void> terminerTrajet(
            @Parameter(description = "Identifiant du trajet", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Identifiant du conducteur effectuant l'action", example = "1")
            @RequestParam Long conducteurId
    ) {
        trajetService.terminerTrajet(id, conducteurId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Annuler un trajet", description = "Annule un trajet et déclenche l'annulation en cascade de toutes ses réservations confirmées.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Trajet et réservations associées annulés avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — le conducteur n'est pas le propriétaire du trajet",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trajet introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Règle métier enfreinte (trajet déjà terminé ou déjà annulé)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerTrajet(
            @Parameter(description = "Identifiant du trajet à annuler", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Identifiant du conducteur effectuant l'annulation", example = "1")
            @RequestParam Long conducteurId) {
        trajetService.annulerTrajet(id, conducteurId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rechercher des trajets disponibles", description = "Recherche paginée de trajets multi-étapes par ville de départ, ville d'arrivée, date, nombre de places et prix maximum optionnel.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Résultats de recherche retournés avec pagination"),
            @ApiResponse(responseCode = "400", description = "Paramètres de recherche invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/recherche")
    public ResponseEntity<Page<TrajetSearchProjection>> rechercherTrajets(
            @Parameter(description = "Ville de départ", example = "Casablanca")
            @RequestParam String villeDepart,
            @Parameter(description = "Ville d'arrivée", example = "Marrakech")
            @RequestParam String villeArrivee,
            @Parameter(description = "Date du trajet (AAAA-MM-JJ)", example = "2026-12-01")
            @RequestParam LocalDate date,
            @Parameter(description = "Nombre de places souhaité", example = "2")
            @RequestParam Integer nombrePlaces,
            @Parameter(description = "Prix maximum par place (optionnel)", example = "150.00")
            @RequestParam(required = false) BigDecimal prixMax,
            @Parameter(description = "Numéro de page (0 par défaut)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page (10 par défaut)", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        TrajetSearchRequestDTO dto = new TrajetSearchRequestDTO(
                villeDepart,
                villeArrivee,
                date,
                nombrePlaces,
                prixMax
        );

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                trajetService.rechercherTrajets(dto, pageable)
        );
    }
}
