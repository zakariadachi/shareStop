
package ma.youcode.covoiturage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TrajetRequestDTO(

        @NotNull
        @Positive
        Integer placesProposees,

        @NotNull
        Long conducteurId,

        @NotNull
        Long vehiculeId,

        @NotEmpty
        @Size(min = 2)
        List<@Valid PointArretRequestDTO> pointsArret

) {
}
