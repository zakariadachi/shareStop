package ma.youcode.covoiturage.service.impl;

import ma.youcode.covoiturage.dto.*;
import ma.youcode.covoiturage.entity.*;
import ma.youcode.covoiturage.exception.BadRequestException;
import ma.youcode.covoiturage.exception.BusinessRuleException;
import ma.youcode.covoiturage.exception.ForbiddenException;
import ma.youcode.covoiturage.mapper.PointArretMapper;
import ma.youcode.covoiturage.mapper.TrajetMapper;
import ma.youcode.covoiturage.repository.ConducteurRepository;
import ma.youcode.covoiturage.repository.ReservationRepository;
import ma.youcode.covoiturage.repository.TrajetRepository;
import ma.youcode.covoiturage.repository.VehiculeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrajetServiceImplTest {

    @Mock
    private TrajetRepository trajetRepository;

    @Mock
    private ConducteurRepository conducteurRepository;

    @Mock
    private VehiculeRepository vehiculeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TrajetMapper trajetMapper;

    @Mock
    private PointArretMapper pointArretMapper;

    @InjectMocks
    private TrajetServiceImpl trajetService;

    private Conducteur conducteur;
    private Vehicule vehicule;
    private TrajetRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        conducteur = new Conducteur();
        conducteur.setId(1L);
        conducteur.setNom("El Idrissi");
        conducteur.setPrenom("Zakaria");

        vehicule = new Vehicule();
        vehicule.setId(1L);
        vehicule.setMarque("Dacia");
        vehicule.setModele("Duster");
        vehicule.setCapaciteMaximale(4);
        vehicule.setConducteur(conducteur);

        List<PointArretRequestDTO> points = List.of(
                new PointArretRequestDTO("Casablanca", LocalDateTime.now().plusDays(1), BigDecimal.ZERO, 0),
                new PointArretRequestDTO("Marrakech", LocalDateTime.now().plusDays(1).plusHours(3), new BigDecimal("100.00"), 1)
        );

        requestDTO = new TrajetRequestDTO(3, 1L, 1L, points);
    }

    @Test
    @DisplayName("Création nominale d'un trajet avec succès")
    void creerTrajet_Succes() {
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));

        Trajet entity = new Trajet();
        entity.setId(10L);
        entity.setPlacesProposees(3);
        entity.setPointsArret(new ArrayList<>());

        when(trajetMapper.toEntity(requestDTO)).thenReturn(entity);
        when(pointArretMapper.toEntity(any())).thenReturn(new PointArret());
        when(trajetRepository.save(any(Trajet.class))).thenReturn(entity);

        TrajetResponseDTO responseDTO = new TrajetResponseDTO(
                10L, "Casablanca", "Marrakech", LocalDateTime.now().plusDays(1), 3, 3L, StatutTrajet.PUBLIE, 1L, 1L, List.of()
        );
        when(trajetMapper.toResponseDTO(eq(entity), anyLong())).thenReturn(responseDTO);

        TrajetResponseDTO result = trajetService.creerTrajet(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.statut()).isEqualTo(StatutTrajet.PUBLIE);
        verify(trajetRepository).save(any(Trajet.class));
    }

    @Test
    @DisplayName("Échec : Le véhicule n'appartient pas au conducteur (403 Forbidden)")
    void creerTrajet_VehiculeAutreConducteur_Forbidden() {
        Conducteur autreConducteur = new Conducteur();
        autreConducteur.setId(99L);
        vehicule.setConducteur(autreConducteur);

        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> trajetService.creerTrajet(requestDTO))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Ce véhicule n'appartient pas à ce conducteur");

        verify(trajetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Échec : Places proposées supérieures à la capacité du véhicule (400 Bad Request)")
    void creerTrajet_PlacesDepassentCapacite_BadRequest() {
        TrajetRequestDTO dtoInvalide = new TrajetRequestDTO(5, 1L, 1L, requestDTO.pointsArret());

        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> trajetService.creerTrajet(dtoInvalide))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Le nombre de places proposé dépasse la capacité du véhicule");

        verify(trajetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Annulation en bloc d'un trajet et de ses réservations confirmées")
    void annulerTrajet_AnnuleTrajetEtReservations() {
        Trajet trajet = new Trajet();
        trajet.setId(10L);
        trajet.setConducteur(conducteur);
        trajet.setStatut(StatutTrajet.PUBLIE);

        Reservation res1 = new Reservation();
        res1.setId(101L);
        res1.setStatut(StatutReservation.CONFIRMEE);

        Reservation res2 = new Reservation();
        res2.setId(102L);
        res2.setStatut(StatutReservation.CONFIRMEE);

        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(reservationRepository.findByTrajetIdAndStatut(10L, StatutReservation.CONFIRMEE))
                .thenReturn(List.of(res1, res2));

        trajetService.annulerTrajet(10L, 1L);

        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.ANNULE);
        assertThat(res1.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        assertThat(res2.getStatut()).isEqualTo(StatutReservation.ANNULEE);

        verify(trajetRepository).save(trajet);
        verify(reservationRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Échec : Annulation refusée si l'appelant n'est pas le conducteur propriétaire (403 Forbidden)")
    void annulerTrajet_NonProprietaire_Forbidden() {
        Trajet trajet = new Trajet();
        trajet.setId(10L);
        trajet.setConducteur(conducteur);

        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> trajetService.annulerTrajet(10L, 999L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("vous n'etes pas proprietaire de ce trajet");
    }

    @Test
    @DisplayName("Échec : Impossible de terminer un trajet avant son départ (409 Conflict)")
    void terminerTrajet_AvantDepart_Conflict() {
        Trajet trajet = new Trajet();
        trajet.setId(10L);
        trajet.setConducteur(conducteur);
        trajet.setStatut(StatutTrajet.PUBLIE);

        PointArret depart = new PointArret();
        depart.setOrdre(0);
        depart.setHeurePassage(LocalDateTime.now().plusHours(2));
        trajet.setPointsArret(List.of(depart));

        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> trajetService.terminerTrajet(10L, 1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Impossible de terminer un trajet avant son depart");
    }

    @Test
    @DisplayName("Consultation d'un trajet avec calcul des places restantes")
    void trouverParId_Succes() {
        Trajet trajet = new Trajet();
        trajet.setId(10L);
        trajet.setPlacesProposees(3);
        trajet.setConducteur(conducteur);
        trajet.setVehicule(vehicule);

        PointArret p1 = new PointArret();
        p1.setVille("Casablanca");
        p1.setOrdre(0);
        p1.setHeurePassage(LocalDateTime.now().plusDays(1));
        PointArret p2 = new PointArret();
        p2.setVille("Marrakech");
        p2.setOrdre(1);
        p2.setHeurePassage(LocalDateTime.now().plusDays(1).plusHours(3));
        trajet.setPointsArret(List.of(p1, p2));

        when(trajetRepository.findWithDetailsById(10L)).thenReturn(Optional.of(trajet));
        when(reservationRepository.compterPlacesReservees(10L, StatutReservation.CONFIRMEE)).thenReturn(1L);

        TrajetResponseDTO responseDTO = new TrajetResponseDTO(
                10L, "Casablanca", "Marrakech", p1.getHeurePassage(), 3, 2L, StatutTrajet.PUBLIE, 1L, 1L, List.of()
        );
        when(trajetMapper.toResponseDTO(trajet, 2L)).thenReturn(responseDTO);

        TrajetResponseDTO result = trajetService.trouverParId(10L);

        assertThat(result).isNotNull();
        assertThat(result.placesRestantes()).isEqualTo(2L);
        assertThat(result.villeDepart()).isEqualTo("Casablanca");
        assertThat(result.villeArrivee()).isEqualTo("Marrakech");
    }
}
