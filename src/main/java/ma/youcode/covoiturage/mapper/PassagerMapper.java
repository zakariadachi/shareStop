package ma.youcode.covoiturage.mapper;

import ma.youcode.covoiturage.dto.PassagerRequestDTO;
import ma.youcode.covoiturage.dto.PassagerResponseDTO;
import ma.youcode.covoiturage.entity.Passager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassagerMapper {

    @Mapping(target = "id", ignore = true)
    Passager toEntity(PassagerRequestDTO dto);

    PassagerResponseDTO toResponseDTO(Passager passager);

    @Mapping(target = "id", ignore = true)
    void updateEntity(PassagerRequestDTO dto, @MappingTarget Passager passager);
}