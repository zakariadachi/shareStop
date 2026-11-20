package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données pour la création ou modification d'un passager")
public record PassagerRequestDTO(

        @Schema(description = "Nom de famille du passager", example = "Benjelloun")
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100)
        String nom,

        @Schema(description = "Prénom du passager", example = "Sara")
        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100)
        String prenom,

        @Schema(description = "Adresse email unique", example = "sara.benjelloun@example.com")
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'adresse email est invalide")
        @Size(max = 150)
        String email,

        @Schema(description = "Numéro de téléphone", example = "+212698765432")
        @NotBlank(message = "Le téléphone est obligatoire")
        @Size(max = 20)
        String telephone
) {
}
