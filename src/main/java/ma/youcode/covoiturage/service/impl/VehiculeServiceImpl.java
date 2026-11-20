
package ma.youcode.covoiturage.service.impl;

import lombok.extern.slf4j.Slf4j;
import ma.youcode.covoiturage.dto.VehiculeRequestDTO;
import ma.youcode.covoiturage.dto.VehiculeResponseDTO;
import ma.youcode.covoiturage.entity.Conducteur;
import ma.youcode.covoiturage.entity.Vehicule;
import ma.youcode.covoiturage.exception.BusinessRuleException;
import ma.youcode.covoiturage.exception.ResourceNotFoundException;
import ma.youcode.covoiturage.mapper.VehiculeMapper;
import ma.youcode.covoiturage.repository.ConducteurRepository;
import ma.youcode.covoiturage.repository.TrajetRepository;
import ma.youcode.covoiturage.repository.VehiculeRepository;
import ma.youcode.covoiturage.service.VehiculeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final ConducteurRepository conducteurRepository;
    private final TrajetRepository trajetRepository;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeServiceImpl(
            VehiculeRepository vehiculeRepository,
            ConducteurRepository conducteurRepository,
            TrajetRepository trajetRepository,
            VehiculeMapper vehiculeMapper
    ) {
        this.vehiculeRepository = vehiculeRepository;
        this.conducteurRepository = conducteurRepository;
        this.trajetRepository = trajetRepository;
        this.vehiculeMapper = vehiculeMapper;
    }

    @Override
    public VehiculeResponseDTO creerVehicule(VehiculeRequestDTO dto) {
        log.info("Création d'un véhicule immatriculé {} pour le conducteur ID {}", dto.immatriculation(), dto.conducteurId());
        Conducteur conducteur = conducteurRepository.findById(dto.conducteurId())
                .orElseThrow(() -> {
                    log.warn("Création de véhicule refusée : conducteur ID {} introuvable", dto.conducteurId());
                    return new ResourceNotFoundException("Conducteur introuvable");
                });

        Vehicule vehicule = vehiculeMapper.toEntity(dto);
        vehicule.setConducteur(conducteur);

        VehiculeResponseDTO response = vehiculeMapper.toResponseDTO(vehiculeRepository.save(vehicule));
        log.info("Véhicule ID {} créé avec succès", response.id());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponseDTO findById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Véhicule introuvable avec l'ID {}", id);
                    return new ResourceNotFoundException("Véhicule introuvable");
                });

        return vehiculeMapper.toResponseDTO(vehicule);
    }

    @Override
    public VehiculeResponseDTO modifierVehicule(Long id, VehiculeRequestDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Modification impossible : véhicule ID {} introuvable", id);
                    return new ResourceNotFoundException("Véhicule introuvable");
                });

        Conducteur conducteur = conducteurRepository.findById(dto.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable"));

        vehiculeMapper.updateEntity(dto, vehicule);
        vehicule.setConducteur(conducteur);

        log.info("Véhicule ID {} modifié avec succès", id);
        return vehiculeMapper.toResponseDTO(vehiculeRepository.save(vehicule));
    }

    @Override
    public void supprimerVehicule(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        if (trajetRepository.existsByVehiculeId(id)) {
            log.warn("Suppression refusée : le véhicule ID {} est associé à un ou plusieurs trajets", id);
            throw new BusinessRuleException("Impossible de supprimer un véhicule associé à un ou plusieurs trajets");
        }

        vehiculeRepository.delete(vehicule);
        log.info("Véhicule ID {} supprimé avec succès", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponseDTO> findByConducteurId(Long conducteurId) {
        if (!conducteurRepository.existsById(conducteurId)) {
            throw new ResourceNotFoundException("Conducteur introuvable");
        }
        return vehiculeRepository.findByConducteurId(conducteurId)
                .stream()
                .map(vehiculeMapper::toResponseDTO)
                .toList();
    }
}
