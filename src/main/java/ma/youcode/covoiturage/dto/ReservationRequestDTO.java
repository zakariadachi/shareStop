
package ma.youcode.covoiturage.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequestDTO(

        @NotNull
        Long passagerId,

        @NotNull
        Long trajetId,

        @NotNull
        Long pointDepartId,

        @NotNull
        Long pointArriveeId,

        @NotNull
        @Positive
        Integer nombrePlaces

) {
}