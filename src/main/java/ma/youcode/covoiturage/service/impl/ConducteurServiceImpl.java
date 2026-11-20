package ma.youcode.covoiturage.service.impl;


import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;
import ma.youcode.covoiturage.entity.Conducteur;
import ma.youcode.covoiturage.exception.ResourceNotFoundException;
import ma.youcode.covoiturage.mapper.ConducteurMapper;
import ma.youcode.covoiturage.repository.ConducteurRepository;
import lombok.extern.slf4j.Slf4j;
import ma.youcode.covoiturage.service.ConducteurService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ConducteurServiceImpl implements ConducteurService {

    private final ConducteurRepository conducteurRepository;
    private final ConducteurMapper conducteurMapper;

    public ConducteurServiceImpl(
            ConducteurRepository conducteurRepository,
            ConducteurMapper conducteurMapper
    ){
        this.conducteurRepository = conducteurRepository;
        this.conducteurMapper = conducteurMapper;
    }

    @Override
    @Transactional
    public ConducteurResponseDTO creerConducteur(ConducteurRequestDTO dto){
        log.info("Création d'un nouveau conducteur avec l'email: {}", dto.email());
        Conducteur conducteur = conducteurMapper.toEntity(dto);

        Conducteur conducteurEnregistre = conducteurRepository.save(conducteur);

        log.info("Conducteur créé avec succès, ID: {}", conducteurEnregistre.getId());
        return  conducteurMapper.toResponseDTO(conducteurEnregistre);
    }

    @Override
    public ConducteurResponseDTO findById(Long id) throws ResourceNotFoundException {
        Conducteur conducteur = conducteurRepository.findById(id).orElseThrow(() -> {
            log.warn("Conducteur introuvable avec l'ID: {}", id);
            return new ResourceNotFoundException("Conducteur introuvable");
        });
        return conducteurMapper.toResponseDTO(conducteur);
    }

    @Override
    public List<ConducteurResponseDTO> findAll() {
        return conducteurRepository.findAll()
                .stream()
                .map(conducteurMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ConducteurResponseDTO modifierConducteur(Long id, ConducteurRequestDTO dto) {
        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Modification impossible: conducteur introuvable avec l'ID: {}", id);
                    return new ResourceNotFoundException("Conducteur introuvable");
                });

        conducteurMapper.updateEntity(dto, conducteur);

        Conducteur conducteurModifie = conducteurRepository.save(conducteur);
        log.info("Conducteur ID {} modifié avec succès", id);

        return conducteurMapper.toResponseDTO(conducteurModifie);
    }

    @Override
    public List<ConducteurResponseDTO> rechercher(String query) {
        return conducteurRepository.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(conducteurMapper::toResponseDTO)
                .toList();
    }
}
