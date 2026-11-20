package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ma.youcode.covoiturage.entity.StatutTrajet;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Détails complets d'un trajet multi-étapes")
public record TrajetResponseDTO(
        @Schema(description = "Identifiant unique du trajet", example = "1")
        Long id,

        @Schema(description = "Ville de départ calculée", example = "Casablanca")
        String villeDepart,

        @Schema(description = "Ville d'arrivée calculée", example = "Marrakech")
        String villeArrivee,

        @Schema(description = "Date et heure de départ calculée", example = "2026-12-01T08:00:00")
        LocalDateTime dateDepart,

        @Schema(description = "Nombre initial de places proposées", example = "3")
        Integer placesProposees,

        @Schema(description = "Nombre de places restantes disponibles à la réservation", example = "3")
        Long placesRestantes,

        @Schema(description = "Statut actuel du trajet", example = "PUBLIE")
        StatutTrajet statut,

        @Schema(description = "Identifiant du conducteur", example = "1")
        Long conducteurId,

        @Schema(description = "Identifiant du véhicule", example = "1")
        Long vehiculeId,

        @Schema(description = "Liste des points d'arrêt ordonnés")
        List<PointArretResponseDTO> pointsArret
) {
}
