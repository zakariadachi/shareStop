
package ma.youcode.covoiturage.dto;

import ma.youcode.covoiturage.entity.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponseDTO(
        Long id,
        Integer nombrePlaces,
        BigDecimal montantTotal,
        LocalDateTime dateReservation,
        StatutReservation statut,
        Long passagerId,
        Long trajetId,
        Long pointDepartId,
        String villeDepart,
        Long pointArriveeId,
        String villeArrivee
) {
}