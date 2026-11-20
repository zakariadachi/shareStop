package ma.youcode.covoiturage.service.impl;

import lombok.extern.slf4j.Slf4j;
import ma.youcode.covoiturage.dto.PassagerRequestDTO;
import ma.youcode.covoiturage.dto.PassagerResponseDTO;
import ma.youcode.covoiturage.entity.Passager;
import ma.youcode.covoiturage.exception.ResourceNotFoundException;
import ma.youcode.covoiturage.mapper.PassagerMapper;
import ma.youcode.covoiturage.repository.PassagerRepository;
import ma.youcode.covoiturage.service.PassagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class PassagerServiceImpl implements PassagerService {

    private final PassagerRepository passagerRepository;
    private final PassagerMapper passagerMapper;

    public PassagerServiceImpl(
            PassagerRepository passagerRepository,
            PassagerMapper passagerMapper
    ) {
        this.passagerRepository = passagerRepository;
        this.passagerMapper = passagerMapper;
    }

    @Override
    public PassagerResponseDTO creerPassager(PassagerRequestDTO dto) {
        log.info("Création d'un passager avec l'email {}", dto.email());
        Passager passager = passagerMapper.toEntity(dto);
        Passager enregistre = passagerRepository.save(passager);
        log.info("Passager ID {} créé avec succès", enregistre.getId());

        return passagerMapper.toResponseDTO(enregistre);
    }

    @Override
    @Transactional(readOnly = true)
    public PassagerResponseDTO findById(Long id) {
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Passager introuvable avec l'ID {}", id);
                    return new ResourceNotFoundException("Passager introuvable");
                });

        return passagerMapper.toResponseDTO(passager);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassagerResponseDTO> findAll() {
        return passagerRepository.findAll()
                .stream()
                .map(passagerMapper::toResponseDTO)
                .toList();
    }

    @Override
    public PassagerResponseDTO modifierPassager(Long id, PassagerRequestDTO dto) {
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Modification impossible : passager ID {} introuvable", id);
                    return new ResourceNotFoundException("Passager introuvable");
                });

        passagerMapper.updateEntity(dto, passager);
        Passager modifie = passagerRepository.save(passager);
        log.info("Passager ID {} modifié avec succès", id);

        return passagerMapper.toResponseDTO(modifie);
    }

    @Override
    @Transactional(readOnly = true)
    public PassagerResponseDTO findByEmail(String email) {
        Passager passager = passagerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Passager introuvable"));

        return passagerMapper.toResponseDTO(passager);
    }
}