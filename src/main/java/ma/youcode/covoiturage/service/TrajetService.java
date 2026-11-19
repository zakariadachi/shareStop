
package ma.youcode.covoiturage.service;

import ma.youcode.covoiturage.dto.TrajetRequestDTO;
import ma.youcode.covoiturage.dto.TrajetResponseDTO;

import java.util.List;

public interface TrajetService {

    TrajetResponseDTO creerTrajet(TrajetRequestDTO dto);

    TrajetResponseDTO trouverParId(Long id);

    List<TrajetResponseDTO> trouverTous();

    void terminerTrajet(Long trajetId,Long conducteurId);

    void annulerTrajet(Long trajetId, Long conducteurId);
}
