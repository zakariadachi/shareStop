
package ma.youcode.covoiturage.mapper;

import ma.youcode.covoiturage.dto.PointArretRequestDTO;
import ma.youcode.covoiturage.dto.PointArretResponseDTO;
import ma.youcode.covoiturage.entity.PointArret;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointArretMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trajet", ignore = true)
    PointArret toEntity(PointArretRequestDTO dto);

    PointArretResponseDTO toResponseDTO(PointArret pointArret);
}
