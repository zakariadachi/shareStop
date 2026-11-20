package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représentation détaillée d'un conducteur")
public record ConducteurResponseDTO(
        @Schema(description = "Identifiant unique du conducteur", example = "1")
        Long id,

        @Schema(description = "Nom de famille", example = "El Alami")
        String nom,

        @Schema(description = "Prénom", example = "Mohamed")
        String prenom,

        @Schema(description = "Adresse email", example = "m.elalami@example.com")
        String email,

        @Schema(description = "Numéro de téléphone", example = "+212612345678")
        String telephone,

        @Schema(description = "Numéro de permis de conduire", example = "PERMIS-123456")
        String numeroPermis
) {
}
