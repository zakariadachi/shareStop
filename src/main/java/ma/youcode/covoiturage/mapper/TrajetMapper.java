package ma.youcode.covoiturage.mapper;

import ma.youcode.covoiturage.dto.TrajetRequestDTO;
import ma.youcode.covoiturage.dto.TrajetResponseDTO;
import ma.youcode.covoiturage.entity.Trajet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = PointArretMapper.class
)
public interface TrajetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "conducteur", ignore = true)
    @Mapping(target = "vehicule", ignore = true)
    @Mapping(target = "pointsArret", ignore = true)
    Trajet toEntity(TrajetRequestDTO dto);

    @Mapping(target = "conducteurId", source = "trajet.conducteur.id")
    @Mapping(target = "vehiculeId", source = "trajet.vehicule.id")
    @Mapping(target = "placesRestantes", source = "placesRestantes")
    @Mapping(target = "id", source = "trajet.id")
    @Mapping(target = "placesProposees", source = "trajet.placesProposees")
    @Mapping(target = "statut", source = "trajet.statut")
    @Mapping(target = "pointsArret", source = "trajet.pointsArret")
    @Mapping(target = "villeDepart", source = "trajet.villeDepart")
    @Mapping(target = "villeArrivee", source = "trajet.villeArrivee")
    @Mapping(target = "dateDepart", source = "trajet.dateDepart")
    TrajetResponseDTO toResponseDTO(Trajet trajet, Long placesRestantes);

    @Mapping(target = "conducteurId", source = "conducteur.id")
    @Mapping(target = "vehiculeId", source = "vehicule.id")
    @Mapping(target = "placesRestantes", expression = "java(trajet.getPlacesProposees() != null ? Long.valueOf(trajet.getPlacesProposees()) : 0L)")
    TrajetResponseDTO toResponseDTO(Trajet trajet);
}
