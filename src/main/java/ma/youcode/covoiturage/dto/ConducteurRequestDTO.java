package ma.youcode.covoiturage.dto;

public record ConducteurRequestDTO
    (String nom,
    String prenom,
    String email,
    String telephone,
    String numeroPermis) {
}
