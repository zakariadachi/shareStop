package ma.youcode.covoiturage.service.impl;

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
        Passager passager = passagerMapper.toEntity(dto);
        Passager enregistre = passagerRepository.save(passager);

        return passagerMapper.toResponseDTO(enregistre);
    }

    @Override
    @Transactional(readOnly = true)
    public PassagerResponseDTO findById(Long id) {
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passager introuvable"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Passager introuvable"));

        passagerMapper.updateEntity(dto, passager);
        Passager modifie = passagerRepository.save(passager);

        return passagerMapper.toResponseDTO(modifie);
    }

    @Override
    public void supprimerPassager(Long id) {
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passager introuvable"));

        passagerRepository.delete(passager);
    }

    @Override
    @Transactional(readOnly = true)
    public PassagerResponseDTO findByEmail(String email) {
        Passager passager = passagerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Passager introuvable"));

        return passagerMapper.toResponseDTO(passager);
    }
}