
package ma.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointArretResponseDTO(
        Long id,
        String ville,
        LocalDateTime heurePassage,
        BigDecimal prixDepuisDepart,
        Integer ordre
) {
}