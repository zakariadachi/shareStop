
package ma.youcode.covoiturage.mapper;

import ma.youcode.covoiturage.dto.ReservationRequestDTO;
import ma.youcode.covoiturage.dto.ReservationResponseDTO;
import ma.youcode.covoiturage.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "dateReservation", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "passager", ignore = true)
    @Mapping(target = "trajet", ignore = true)
    @Mapping(target = "pointDepart", ignore = true)
    @Mapping(target = "pointArrivee", ignore = true)
    Reservation toEntity(ReservationRequestDTO dto);

    @Mapping(target = "passagerId", source = "passager.id")
    @Mapping(target = "trajetId", source = "trajet.id")
    @Mapping(target = "pointDepartId", source = "pointDepart.id")
    @Mapping(target = "villeDepart", source = "pointDepart.ville")
    @Mapping(target = "pointArriveeId", source = "pointArrivee.id")
    @Mapping(target = "villeArrivee", source = "pointArrivee.ville")
    ReservationResponseDTO toResponseDTO(Reservation reservation);
}
