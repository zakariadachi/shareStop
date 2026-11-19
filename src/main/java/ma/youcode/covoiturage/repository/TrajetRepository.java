
package ma.youcode.covoiturage.repository;

import ma.youcode.covoiturage.entity.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {
}
