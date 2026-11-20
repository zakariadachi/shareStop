package ma.youcode.covoiturage.service;

import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;

import java.util.List;

public interface ConducteurService {

    ConducteurResponseDTO creerConducteur(ConducteurRequestDTO dto);

    ConducteurResponseDTO findById(Long id);

    List<ConducteurResponseDTO> findAll();

    ConducteurResponseDTO modifierConducteur(Long id, ConducteurRequestDTO dto);

    List<ConducteurResponseDTO> rechercher(String query);
}