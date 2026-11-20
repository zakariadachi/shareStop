package ma.youcode.covoiturage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "trajets")
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "places_proposees", nullable = false)
    private Integer placesProposees;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutTrajet statut = StatutTrajet.PUBLIE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @OneToMany(mappedBy = "trajet", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<PointArret> pointsArret = new ArrayList<>();

    @Transient
    public String getVilleDepart() {
        if (pointsArret == null || pointsArret.isEmpty()) return null;
        return pointsArret.get(0).getVille();
    }

    @Transient
    public String getVilleArrivee() {
        if (pointsArret == null || pointsArret.isEmpty()) return null;
        return pointsArret.get(pointsArret.size() - 1).getVille();
    }

    @Transient
    public LocalDateTime getDateDepart() {
        if (pointsArret == null || pointsArret.isEmpty()) return null;
        return pointsArret.get(0).getHeurePassage();
    }
}