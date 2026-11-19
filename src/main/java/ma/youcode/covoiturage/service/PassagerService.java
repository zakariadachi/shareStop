package ma.youcode.covoiturage.service;

import ma.youcode.covoiturage.dto.PassagerRequestDTO;
import ma.youcode.covoiturage.dto.PassagerResponseDTO;

import java.util.List;

public interface PassagerService {

    PassagerResponseDTO creerPassager(PassagerRequestDTO dto);

    PassagerResponseDTO findById(Long id);

    List<PassagerResponseDTO> findAll();

    PassagerResponseDTO modifierPassager(Long id, PassagerRequestDTO dto);

    void supprimerPassager(Long id);

    PassagerResponseDTO findByEmail(String email);
}