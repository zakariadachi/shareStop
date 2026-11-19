
package ma.youcode.covoiturage.dto;

public record VehiculeResponseDTO(
        Long id,
        String marque,
        String modele,
        String immatriculation,
        String couleur,
        Integer capaciteMaximale,
        Long conducteurId
) {
}
