
package ma.youcode.covoiturage.service;

import ma.youcode.covoiturage.dto.VehiculeRequestDTO;
import ma.youcode.covoiturage.dto.VehiculeResponseDTO;

import java.util.List;

public interface VehiculeService {

    VehiculeResponseDTO creerVehicule(VehiculeRequestDTO dto);

    VehiculeResponseDTO findById(Long id);

    VehiculeResponseDTO modifierVehicule(Long id, VehiculeRequestDTO dto);

    void supprimerVehicule(Long id);

    List<VehiculeResponseDTO> findByConducteurId(Long conducteurId);
}
