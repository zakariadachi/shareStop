package ma.youcode.covoiturage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConducteurRequestDTO(
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "le nom ne doit pas dépasser 100 caractères")
    String nom,

    @NotBlank(message = "le prenom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    String prenom,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'adresse email est invalide")
    @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
    String email,

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    String telephone,

    @NotBlank(message = "Le numéro de permis est obligatoire")
    @Size(max = 50, message = "Le numéro de permis ne doit pas dépasser 50")
    String numeroPermis
) {
}
