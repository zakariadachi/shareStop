
package ma.youcode.covoiturage.repository;

import ma.youcode.covoiturage.dto.TrajetSearchProjection;
import ma.youcode.covoiturage.entity.StatutReservation;
import ma.youcode.covoiturage.entity.StatutTrajet;
import ma.youcode.covoiturage.entity.Trajet;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    @EntityGraph(attributePaths = {
            "conducteur",
            "vehicule",
            "pointsArret"
    })
    @Query("select t from Trajet t where t.id = :id")
    Optional<Trajet> findWithDetailsById(@Param("id") Long id);

    boolean existsByVehiculeId(Long vehiculeId);



    @Query(
            value = """
        select
            t.id as id,
            depart.ville as villeDepart,
            arrivee.ville as villeArrivee,
            depart.heurePassage as heureDepart,
            arrivee.heurePassage as heureArrivee,
            (arrivee.prixDepuisDepart - depart.prixDepuisDepart) as prix,
            (t.placesProposees - coalesce(
                (select sum (r.nombrePlaces)
                 from Reservation r
                 where r.trajet.id = t.id
                 and r.statut = :statutReservation),
                0
            ) ) as placesDisponibles
        from Trajet t
        join t.pointsArret depart
        join t.pointsArret arrivee
        where lower(depart.ville) = lower(:villeDepart)
        and lower(arrivee.ville) = lower(:villeArrivee)
        and depart.ordre < arrivee.ordre
        and depart.heurePassage >= :debutJournee
        and depart.heurePassage < :finJournee
        and t.statut = :statutTrajet
        and (
            t.placesProposees - COALESCE(
                (select SUM(r.nombrePlaces)
                 from Reservation r
                 where r.trajet.id = t.id
                 and r.statut = :statutReservation),
                0
            )
        ) >= :nombrePlaces
        and (:prixMax is null or
            (arrivee.prixDepuisDepart - depart.prixDepuisDepart) <= :prixMax)
        order by depart.heurePassage asc
        """,
            countQuery = """
        select COUNT(distinct t.id)
        from Trajet t
        JOIN t.pointsArret depart
        JOIN t.pointsArret arrivee
        where lower(depart.ville) = lower(:villeDepart)
        and lower(arrivee.ville) = lower(:villeArrivee)
        and depart.ordre < arrivee.ordre
        and depart.heurePassage >= :debutJournee
        and depart.heurePassage < :finJournee
        and t.statut = :statutTrajet
        and (
            t.placesProposees - COALESCE(
                (select SUM(r.nombrePlaces)
                 from Reservation r
                 where r.trajet.id = t.id
                 and r.statut = :statutReservation),
                0
            )
        ) >= :nombrePlaces
        and (:prixMax is null or
            (arrivee.prixDepuisDepart - depart.prixDepuisDepart) <= :prixMax)
        """
    )
    Page<TrajetSearchProjection> rechercherTrajets(
            @Param("villeDepart") String villeDepart,
            @Param("villeArrivee") String villeArrivee,
            @Param("debutJournee") LocalDateTime debutJournee,
            @Param("finJournee") LocalDateTime finJournee,
            @Param("nombrePlaces") Integer nombrePlaces,
            @Param("prixMax") BigDecimal prixMax,
            @Param("statutTrajet") StatutTrajet statutTrajet,
            @Param("statutReservation") StatutReservation statutReservation,
            Pageable pageable
    );
}
