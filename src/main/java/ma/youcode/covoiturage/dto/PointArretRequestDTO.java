package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Point d'arrêt intermédiaire ou terminus d'un trajet")
public record PointArretRequestDTO(

        @Schema(description = "Nom de la ville de l'arrêt", example = "Casablanca")
        @NotBlank
        @Size(max = 100)
        String ville,

        @Schema(description = "Date et heure de passage prévue à cet arrêt", example = "2026-12-01T08:00:00")
        @NotNull
        LocalDateTime heurePassage,

        @Schema(description = "Prix cumulé depuis la ville de départ (0 pour le départ)", example = "0.00")
        @NotNull
        @DecimalMin("0.00")
        BigDecimal prixDepuisDepart,

        @Schema(description = "Ordre séquentiel de l'arrêt (commence à 0 pour le départ)", example = "0")
        @NotNull
        @PositiveOrZero
        Integer ordre

) {
}
