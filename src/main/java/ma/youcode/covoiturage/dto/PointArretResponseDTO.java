package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Détails d'un point d'arrêt d'un trajet")
public record PointArretResponseDTO(
        @Schema(description = "Identifiant unique du point d'arrêt", example = "1")
        Long id,

        @Schema(description = "Nom de la ville", example = "Casablanca")
        String ville,

        @Schema(description = "Date et heure de passage", example = "2026-12-01T08:00:00")
        LocalDateTime heurePassage,

        @Schema(description = "Prix cumulé depuis le départ", example = "0.00")
        BigDecimal prixDepuisDepart,

        @Schema(description = "Ordre séquentiel de l'arrêt", example = "0")
        Integer ordre
) {
}
