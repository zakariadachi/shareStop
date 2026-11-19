
package ma.youcode.covoiturage.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointArretRequestDTO(

        @NotBlank
        @Size(max = 100)
        String ville,

        @NotNull
        LocalDateTime heurePassage,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal prixDepuisDepart,

        @NotNull
        @PositiveOrZero
        Integer ordre

) {
}