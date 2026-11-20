package ma.youcode.covoiturage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.youcode.covoiturage.dto.ReservationRequestDTO;
import ma.youcode.covoiturage.dto.ReservationResponseDTO;
import ma.youcode.covoiturage.exception.ErrorResponse;
import ma.youcode.covoiturage.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Réservations", description = "Gestion des réservations : création, consultation et annulation par le passager")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Créer une réservation", description = "Enregistre une réservation de places pour un trajet donné entre deux points d'arrêt. Calcule le montant total, vérifie la disponibilité des places et met à jour le statut du trajet en COMPLET si toutes les places sont réservées.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou point de départ postérieur ou égal au point d'arrivée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Passager, trajet ou point d'arrêt introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Règle métier enfreinte (trajet non disponible, réservation déjà existante pour ce passager, ou nombre de places insuffisant)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> creerReservation(@Valid @RequestBody ReservationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.creerReservation(dto));
    }

    @Operation(summary = "Trouver une réservation par ID", description = "Retourne les détails d'une réservation à partir de son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> trouverParId(
            @Parameter(description = "Identifiant de la réservation", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.trouverParId(id));
    }

    @Operation(summary = "Lister les réservations d'un passager", description = "Retourne l'ensemble des réservations effectuées par un passager.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des réservations du passager retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Passager introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/passager/{passagerId}")
    public ResponseEntity<List<ReservationResponseDTO>> trouverParPassager(
            @Parameter(description = "Identifiant du passager", example = "1")
            @PathVariable Long passagerId) {
        return ResponseEntity.ok(reservationService.trouverParPassager(passagerId));
    }

    @Operation(summary = "Lister les réservations d'un conducteur", description = "Retourne l'ensemble des réservations passées sur les trajets proposés par un conducteur.")
    @ApiResponse(responseCode = "200", description = "Liste des réservations retournée avec succès")
    @GetMapping("/conducteur/{conducteurId}")
    public ResponseEntity<List<ReservationResponseDTO>> trouverParConducteur(
            @Parameter(description = "Identifiant du conducteur", example = "1")
            @PathVariable Long conducteurId) {
        return ResponseEntity.ok(reservationService.trouverParConducteur(conducteurId));
    }

    @Operation(summary = "Annuler une réservation", description = "Permet au passager d'annuler sa réservation au moins 24 heures avant le départ. Si le trajet était complet, il repasse au statut PUBLIE.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réservation annulée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — l'utilisateur n'est pas le propriétaire de cette réservation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Règle métier enfreinte (réservation non confirmée ou délai d'annulation de 24h dépassé)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/annuler/{passagerId}")
    public ResponseEntity<Void> annulerReservation(
            @Parameter(description = "Identifiant de la réservation", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Identifiant du passager demandant l'annulation", example = "1")
            @PathVariable Long passagerId) {
        reservationService.annulerReservation(id, passagerId);
        return ResponseEntity.noContent().build();
    }
}
