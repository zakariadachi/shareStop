
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

    @Mapping(target = "conducteurId", source = "conducteur.id")
    @Mapping(target = "vehiculeId", source = "vehicule.id")
    TrajetResponseDTO toResponseDTO(Trajet trajet);
}
