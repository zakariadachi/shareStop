package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ma.youcode.covoiturage.entity.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Représentation détaillée d'une réservation")
public record ReservationResponseDTO(
        @Schema(description = "Identifiant unique de la réservation", example = "1")
        Long id,

        @Schema(description = "Nombre de places réservées", example = "2")
        Integer nombrePlaces,

        @Schema(description = "Montant total calculé pour la réservation", example = "160.00")
        BigDecimal montantTotal,

        @Schema(description = "Date et heure de création de la réservation", example = "2026-11-20T10:30:00")
        LocalDateTime dateReservation,

        @Schema(description = "Statut de la réservation", example = "CONFIRMEE")
        StatutReservation statut,

        @Schema(description = "Identifiant du passager", example = "1")
        Long passagerId,

        @Schema(description = "Identifiant du trajet", example = "1")
        Long trajetId,

        @Schema(description = "Identifiant du point de départ", example = "1")
        Long pointDepartId,

        @Schema(description = "Nom de la ville de départ", example = "Casablanca")
        String villeDepart,

        @Schema(description = "Identifiant du point d'arrivée", example = "2")
        Long pointArriveeId,

        @Schema(description = "Nom de la ville d'arrivée", example = "Marrakech")
        String villeArrivee
) {
}
