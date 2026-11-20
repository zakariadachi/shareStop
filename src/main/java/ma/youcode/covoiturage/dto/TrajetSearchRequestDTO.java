package ma.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TrajetSearchRequestDTO(
        String villeDepart,
        String villeArrivee,
        LocalDate date,
        Integer nombrePlaces,
        BigDecimal prixMax
) {
}
