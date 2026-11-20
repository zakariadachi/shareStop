package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Données pour la création ou modification d'un véhicule")
public record VehiculeRequestDTO(

        @Schema(description = "Marque du véhicule", example = "Dacia")
        @NotBlank(message = "La marque est obligatoire")
        @Size(max = 100)
        String marque,

        @Schema(description = "Modèle du véhicule", example = "Duster")
        @NotBlank(message = "Le modèle est obligatoire")
        @Size(max = 100)
        String modele,

        @Schema(description = "Numéro d'immatriculation unique", example = "12345-A-26")
        @NotBlank(message = "L'immatriculation est obligatoire")
        @Size(max = 20)
        String immatriculation,

        @Schema(description = "Couleur du véhicule", example = "Gris")
        @NotBlank(message = "La couleur est obligatoire")
        @Size(max = 50)
        String couleur,

        @Schema(description = "Capacité maximale en places (entre 1 et 8)", example = "4")
        @NotNull(message = "La capacité maximale est obligatoire")
        @Min(value = 1, message = "La capacité minimale est de 1 place")
        @Max(value = 8, message = "La capacité maximale est de 8 places")
        Integer capaciteMaximale,

        @Schema(description = "Identifiant du conducteur propriétaire du véhicule", example = "1")
        @NotNull(message = "Le conducteur est obligatoire")
        Long conducteurId
) {
}
