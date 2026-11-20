
package ma.youcode.covoiturage.service;

import ma.youcode.covoiturage.dto.TrajetRequestDTO;
import ma.youcode.covoiturage.dto.TrajetResponseDTO;
import ma.youcode.covoiturage.dto.TrajetSearchProjection;
import ma.youcode.covoiturage.dto.TrajetSearchRequestDTO;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface TrajetService {

    TrajetResponseDTO creerTrajet(TrajetRequestDTO dto);

    TrajetResponseDTO trouverParId(Long id);

    void terminerTrajet(Long trajetId,Long conducteurId);

    void annulerTrajet(Long trajetId, Long conducteurId);

    Page<TrajetSearchProjection> rechercherTrajets(TrajetSearchRequestDTO dto, Pageable pageable);
}
