
package ma.youcode.covoiturage.service;

import ma.youcode.covoiturage.dto.ReservationRequestDTO;
import ma.youcode.covoiturage.dto.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {

    ReservationResponseDTO creerReservation(ReservationRequestDTO dto);

    ReservationResponseDTO trouverParId(Long id);

    List<ReservationResponseDTO> trouverParPassager(Long passagerId);

    List<ReservationResponseDTO> trouverParConducteur(Long conducteurId);

    void annulerReservation(Long reservationId, Long passagerId);
}
