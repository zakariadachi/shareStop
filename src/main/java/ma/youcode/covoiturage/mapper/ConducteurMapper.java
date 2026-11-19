package ma.youcode.covoiturage.mapper;


import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;
import ma.youcode.covoiturage.entity.Conducteur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConducteurMapper {
    @Mapping(target = "id", ignore = true)
    Conducteur toEntity(ConducteurRequestDTO dto);

    ConducteurResponseDTO toResponseDTO(Conducteur conducteur);

    void updateEntity(ConducteurRequestDTO dto, @MappingTarget Conducteur conducteur);
}
