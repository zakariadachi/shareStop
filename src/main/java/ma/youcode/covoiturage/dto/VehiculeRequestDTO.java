
package ma.youcode.covoiturage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VehiculeRequestDTO(

        @NotBlank(message = "La marque est obligatoire")
        @Size(max = 100)
        String marque,

        @NotBlank(message = "Le modèle est obligatoire")
        @Size(max = 100)
        String modele,

        @NotBlank(message = "L'immatriculation est obligatoire")
        @Size(max = 20)
        String immatriculation,

        @NotBlank(message = "La couleur est obligatoire")
        @Size(max = 50)
        String couleur,

        @NotNull(message = "La capacité maximale est obligatoire")
        @Positive(message = "La capacité doit être positive")
        Integer capaciteMaximale,

        @NotNull(message = "Le conducteur est obligatoire")
        Long conducteurId
) {
}
