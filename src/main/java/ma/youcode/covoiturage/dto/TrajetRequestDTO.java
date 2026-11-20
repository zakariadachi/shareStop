package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Données pour la création d'un trajet multi-étapes")
public record TrajetRequestDTO(

        @Schema(description = "Nombre de places proposées pour le trajet", example = "3")
        @NotNull
        @Positive
        Integer placesProposees,

        @Schema(description = "Identifiant du conducteur publiant le trajet", example = "1")
        @NotNull
        Long conducteurId,

        @Schema(description = "Identifiant du véhicule utilisé", example = "1")
        @NotNull
        Long vehiculeId,

        @Schema(description = "Liste ordonnée d'au moins 2 points d'arrêt (départ, étapes, arrivée)")
        @NotEmpty
        @Size(min = 2)
        List<@Valid PointArretRequestDTO> pointsArret

) {
}
