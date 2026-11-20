package ma.youcode.covoiturage.service.impl;

import ma.youcode.covoiturage.dto.ReservationRequestDTO;
import ma.youcode.covoiturage.dto.ReservationResponseDTO;
import ma.youcode.covoiturage.entity.*;
import ma.youcode.covoiturage.exception.BadRequestException;
import ma.youcode.covoiturage.exception.BusinessRuleException;
import ma.youcode.covoiturage.exception.ForbiddenException;
import ma.youcode.covoiturage.mapper.ReservationMapper;
import ma.youcode.covoiturage.repository.PassagerRepository;
import ma.youcode.covoiturage.repository.ReservationRepository;
import ma.youcode.covoiturage.repository.TrajetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PassagerRepository passagerRepository;

    @Mock
    private TrajetRepository trajetRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Passager passager;
    private Trajet trajet;
    private PointArret p1;
    private PointArret p2;

    @BeforeEach
    void setUp() {
        passager = new Passager();
        passager.setId(1L);
        passager.setNom("Benani");
        passager.setPrenom("Salma");

        p1 = new PointArret();
        p1.setId(10L);
        p1.setVille("Casablanca");
        p1.setOrdre(0);
        p1.setPrixDepuisDepart(BigDecimal.ZERO);
        p1.setHeurePassage(LocalDateTime.now().plusDays(2));

        p2 = new PointArret();
        p2.setId(20L);
        p2.setVille("Marrakech");
        p2.setOrdre(1);
        p2.setPrixDepuisDepart(new BigDecimal("120.00"));
        p2.setHeurePassage(LocalDateTime.now().plusDays(2).plusHours(3));

        trajet = new Trajet();
        trajet.setId(100L);
        trajet.setStatut(StatutTrajet.PUBLIE);
        trajet.setPlacesProposees(3);
        trajet.setPointsArret(List.of(p1, p2));
    }

    @Test
    @DisplayName("Création nominale : calcul du montant (120 DH * 2 = 240 DH) et sauvegarde")
    void creerReservation_Succes() {
        ReservationRequestDTO dto = new ReservationRequestDTO(1L, 100L, 10L, 20L, 2);

        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findById(100L)).thenReturn(Optional.of(trajet));
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(1L, 100L, StatutReservation.CONFIRMEE))
                .thenReturn(false);
        when(reservationRepository.compterPlacesReservees(100L, StatutReservation.CONFIRMEE))
                .thenReturn(0L);

        Reservation savedReservation = new Reservation();
        savedReservation.setId(500L);
        savedReservation.setMontantTotal(new BigDecimal("240.00"));
        savedReservation.setStatut(StatutReservation.CONFIRMEE);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponseDTO responseDTO = new ReservationResponseDTO(
                500L, 2, new BigDecimal("240.00"), LocalDateTime.now(), StatutReservation.CONFIRMEE,
                1L, 100L, 10L, "Casablanca", 20L, "Marrakech"
        );
        when(reservationMapper.toResponseDTO(savedReservation)).thenReturn(responseDTO);

        ReservationResponseDTO result = reservationService.creerReservation(dto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(500L);
        assertThat(result.montantTotal()).isEqualByComparingTo("240.00");
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Échec : Le point de montée est après la descente (400 Bad Request - Scénario 6)")
    void creerReservation_MonteeApresDescente_BadRequest() {
        ReservationRequestDTO dto = new ReservationRequestDTO(1L, 100L, 20L, 10L, 1);

        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findById(100L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> reservationService.creerReservation(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("le point de depart doit preceder le point d'arriver");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Échec : Nombre de places insuffisant (409 Conflict - Scénario 4)")
    void creerReservation_PlacesInsuffisantes_Conflict() {
        ReservationRequestDTO dto = new ReservationRequestDTO(1L, 100L, 10L, 20L, 4);

        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findById(100L)).thenReturn(Optional.of(trajet));
        when(reservationRepository.compterPlacesReservees(100L, StatutReservation.CONFIRMEE))
                .thenReturn(0L);

        assertThatThrownBy(() -> reservationService.creerReservation(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Nombre de places insuffisant");
    }

    @Test
    @DisplayName("Annulation réussie plus de 24h avant départ et bascule de COMPLET à PUBLIE (Scénario 5)")
    void annulerReservation_Succes_BasculeCompletVersPublie() {
        trajet.setStatut(StatutTrajet.COMPLET);

        Reservation reservation = new Reservation();
        reservation.setId(500L);
        reservation.setPassager(passager);
        reservation.setTrajet(trajet);
        reservation.setPointDepart(p1); // p1 est dans 2 jours
        reservation.setStatut(StatutReservation.CONFIRMEE);

        when(reservationRepository.findById(500L)).thenReturn(Optional.of(reservation));

        reservationService.annulerReservation(500L, 1L);

        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
        verify(reservationRepository).save(reservation);
        verify(trajetRepository).save(trajet);
    }

    @Test
    @DisplayName("Échec : Annulation refusée moins de 24h avant le départ (409 Conflict - Scénario 10)")
    void annulerReservation_MoinsDe24h_Conflict() {
        p1.setHeurePassage(LocalDateTime.now().plusHours(2)); // dans 2 heures

        Reservation reservation = new Reservation();
        reservation.setId(500L);
        reservation.setPassager(passager);
        reservation.setTrajet(trajet);
        reservation.setPointDepart(p1);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        when(reservationRepository.findById(500L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.annulerReservation(500L, 1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("l'annulation est possible uniquement jusqu'a 24 heures avant le depart");
    }

    @Test
    @DisplayName("Échec : Annulation par un utilisateur qui n'est pas le propriétaire (403 Forbidden)")
    void annulerReservation_NonProprietaire_Forbidden() {
        Reservation reservation = new Reservation();
        reservation.setId(500L);
        reservation.setPassager(passager);

        when(reservationRepository.findById(500L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.annulerReservation(500L, 999L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("vous n'etes pas le proprietaire de cette reservation");
    }
}
