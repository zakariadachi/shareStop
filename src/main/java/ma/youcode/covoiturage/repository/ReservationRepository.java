
package ma.youcode.covoiturage.repository;

import ma.youcode.covoiturage.entity.Reservation;
import ma.youcode.covoiturage.entity.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByPassagerIdAndTrajetIdAndStatut(
            Long passagerId,
            Long trajetId,
            StatutReservation statut
    );

    List<Reservation> findByPassagerId(Long passagerId);

    @Query("""
            SELECT COALESCE(SUM(r.nombrePlaces), 0)
            FROM Reservation r
            WHERE r.trajet.id = :trajetId
            AND r.statut = :statut
            """)
    Long compterPlacesReservees(@Param("trajetId") Long trajetId, @Param("statut") StatutReservation statut);
}
