package ma.youcode.covoiturage.service;

import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;

public interface ConducteurService {

    ConducteurResponseDTO creerConducteur(ConducteurRequestDTO dto);

    ConducteurResponseDTO findById(Long id);
}