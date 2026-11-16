package ma.youcode.covoiturage.mapper;


import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;
import ma.youcode.covoiturage.entity.Conducteur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConducteurMapper {

    Conducteur toEntity(ConducteurRequestDTO dto);

    ConducteurResponseDTO toResponseDTO(Conducteur conducteur);
}
