package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données pour la création ou modification d'un conducteur")
public record ConducteurRequestDTO(
    @Schema(description = "Nom de famille du conducteur", example = "El Alami")
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "le nom ne doit pas dépasser 100 caractères")
    String nom,

    @Schema(description = "Prénom du conducteur", example = "Mohamed")
    @NotBlank(message = "le prenom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    String prenom,

    @Schema(description = "Adresse email unique", example = "m.elalami@example.com")
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'adresse email est invalide")
    @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
    String email,

    @Schema(description = "Numéro de téléphone", example = "+212612345678")
    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    String telephone,

    @Schema(description = "Numéro du permis de conduire", example = "PERMIS-123456")
    @NotBlank(message = "Le numéro de permis est obligatoire")
    @Size(max = 50, message = "Le numéro de permis ne doit pas dépasser 50")
    String numeroPermis
) {
}
