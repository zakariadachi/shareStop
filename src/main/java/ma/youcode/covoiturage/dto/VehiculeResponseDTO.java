package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représentation détaillée d'un véhicule")
public record VehiculeResponseDTO(
        @Schema(description = "Identifiant unique du véhicule", example = "1")
        Long id,

        @Schema(description = "Marque du véhicule", example = "Dacia")
        String marque,

        @Schema(description = "Modèle du véhicule", example = "Duster")
        String modele,

        @Schema(description = "Numéro d'immatriculation", example = "12345-A-26")
        String immatriculation,

        @Schema(description = "Couleur du véhicule", example = "Gris")
        String couleur,

        @Schema(description = "Capacité maximale en places", example = "4")
        Integer capaciteMaximale,

        @Schema(description = "Identifiant du conducteur propriétaire", example = "1")
        Long conducteurId
) {
}
