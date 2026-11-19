
package ma.youcode.covoiturage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "points_arret")
public class PointArret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(name = "heure_passage", nullable = false)
    private LocalDateTime heurePassage;

    @Column(name = "prix_depuis_depart", nullable = false, precision = 6, scale = 2)
    private BigDecimal prixDepuisDepart;

    @Column(nullable = false)
    private Integer ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;
}
