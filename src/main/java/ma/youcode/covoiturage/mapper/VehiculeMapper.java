
package ma.youcode.covoiturage.mapper;

import ma.youcode.covoiturage.dto.VehiculeRequestDTO;
import ma.youcode.covoiturage.dto.VehiculeResponseDTO;
import ma.youcode.covoiturage.entity.Vehicule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conducteur", ignore = true)
    Vehicule toEntity(VehiculeRequestDTO dto);

    @Mapping(target = "conducteurId", source = "conducteur.id")
    VehiculeResponseDTO toResponseDTO(Vehicule vehicule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conducteur", ignore = true)
    void updateEntity(VehiculeRequestDTO dto, @MappingTarget Vehicule vehicule);
}
