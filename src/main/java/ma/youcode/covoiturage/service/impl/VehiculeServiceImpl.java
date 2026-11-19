
package ma.youcode.covoiturage.service.impl;

import ma.youcode.covoiturage.dto.VehiculeRequestDTO;
import ma.youcode.covoiturage.dto.VehiculeResponseDTO;
import ma.youcode.covoiturage.entity.Conducteur;
import ma.youcode.covoiturage.entity.Vehicule;
import ma.youcode.covoiturage.exception.ResourceNotFoundException;
import ma.youcode.covoiturage.mapper.VehiculeMapper;
import ma.youcode.covoiturage.repository.ConducteurRepository;
import ma.youcode.covoiturage.repository.VehiculeRepository;
import ma.youcode.covoiturage.service.VehiculeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeServiceImpl(
            VehiculeRepository vehiculeRepository,
            ConducteurRepository conducteurRepository,
            VehiculeMapper vehiculeMapper
    ) {
        this.vehiculeRepository = vehiculeRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeMapper = vehiculeMapper;
    }

    @Override
    public VehiculeResponseDTO creerVehicule(VehiculeRequestDTO dto) {
        Conducteur conducteur = conducteurRepository.findById(dto.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable"));

        Vehicule vehicule = vehiculeMapper.toEntity(dto);
        vehicule.setConducteur(conducteur);

        return vehiculeMapper.toResponseDTO(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponseDTO> findAll() {
        return vehiculeRepository.findAll()
                .stream()
                .map(vehiculeMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponseDTO findById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        return vehiculeMapper.toResponseDTO(vehicule);
    }

    @Override
    public VehiculeResponseDTO modifierVehicule(Long id, VehiculeRequestDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        Conducteur conducteur = conducteurRepository.findById(dto.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable"));

        vehiculeMapper.updateEntity(dto, vehicule);
        vehicule.setConducteur(conducteur);

        return vehiculeMapper.toResponseDTO(vehiculeRepository.save(vehicule));
    }

    @Override
    public void supprimerVehicule(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        vehiculeRepository.delete(vehicule);
    }
}
