package ma.youcode.covoiturage.service;


import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;
import ma.youcode.covoiturage.mapper.ConducteurMapper;
import ma.youcode.covoiturage.repository.ConducteurRepository;
import org.springframework.stereotype.Service;


public interface ConducteurService {

    ConducteurResponseDTO creerConducteur(ConducteurRequestDTO dto);

}
