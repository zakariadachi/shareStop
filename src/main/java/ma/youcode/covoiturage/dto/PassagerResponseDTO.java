package ma.youcode.covoiturage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représentation détaillée d'un passager")
public record PassagerResponseDTO(
        @Schema(description = "Identifiant unique du passager", example = "1")
        Long id,

        @Schema(description = "Nom de famille", example = "Benjelloun")
        String nom,

        @Schema(description = "Prénom", example = "Sara")
        String prenom,

        @Schema(description = "Adresse email", example = "sara.benjelloun@example.com")
        String email,

        @Schema(description = "Numéro de téléphone", example = "+212698765432")
        String telephone
) {
}
