package ma.youcode.covoiturage.dto;

public record PassagerResponseDTO(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone
) {
}