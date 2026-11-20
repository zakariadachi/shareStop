package ma.youcode.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TrajetSearchProjection {
    Long getId();

    String getVilleDepart();

    String getVilleArrivee();

    LocalDateTime getHeureDepart();

    LocalDateTime getHeureArrivee();

    BigDecimal getPrix();

    Long getPlacesDisponibles();
}
