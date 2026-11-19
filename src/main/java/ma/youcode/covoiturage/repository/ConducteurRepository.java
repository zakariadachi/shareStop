package ma.youcode.covoiturage.repository;


import ma.youcode.covoiturage.dto.ConducteurResponseDTO;
import ma.youcode.covoiturage.entity.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;
import ma.youcode.covoiturage.entity.Conducteur;

import java.util.Optional;

public interface ConducteurRepository extends JpaRepository<Conducteur, Long>{
}
