
package ma.youcode.covoiturage.dto;

import ma.youcode.covoiturage.entity.StatutTrajet;

import java.util.List;

public record TrajetResponseDTO(
        Long id,
        Integer placesProposees,
        StatutTrajet statut,
        Long conducteurId,
        Long vehiculeId,
        List<PointArretResponseDTO> pointsArret
) {
}