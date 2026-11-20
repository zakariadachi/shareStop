package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Données pour la création d'une réservation")
public record ReservationRequestDTO(

        @Schema(description = "Identifiant du passager", example = "1")
        @NotNull
        Long passagerId,

        @Schema(description = "Identifiant du trajet", example = "1")
        @NotNull
        Long trajetId,

        @Schema(description = "Identifiant du point d'arrêt de départ (montée)", example = "1")
        @NotNull
        Long pointDepartId,

        @Schema(description = "Identifiant du point d'arrêt d'arrivée (descente)", example = "2")
        @NotNull
        Long pointArriveeId,

        @Schema(description = "Nombre de places à réserver", example = "2")
        @NotNull
        @Positive
        Integer nombrePlaces

) {
}
