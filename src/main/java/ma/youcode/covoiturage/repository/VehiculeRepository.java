
package ma.youcode.covoiturage.repository;

import ma.youcode.covoiturage.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    List<Vehicule> findByConducteurId(Long conducteurId);
}
