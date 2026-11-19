package ma.youcode.covoiturage.repository;


import ma.youcode.covoiturage.entity.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConducteurRepository extends JpaRepository<Conducteur, Long>{
}
