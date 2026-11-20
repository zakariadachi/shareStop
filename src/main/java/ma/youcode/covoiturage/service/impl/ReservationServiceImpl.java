package ma.youcode.covoiturage.service.impl;

import lombok.extern.slf4j.Slf4j;
import ma.youcode.covoiturage.dto.ReservationRequestDTO;
import ma.youcode.covoiturage.dto.ReservationResponseDTO;
import ma.youcode.covoiturage.entity.*;
import ma.youcode.covoiturage.exception.BadRequestException;
import ma.youcode.covoiturage.exception.BusinessRuleException;
import ma.youcode.covoiturage.exception.ForbiddenException;
import ma.youcode.covoiturage.exception.ResourceNotFoundException;
import ma.youcode.covoiturage.mapper.ReservationMapper;
import ma.youcode.covoiturage.repository.PassagerRepository;
import ma.youcode.covoiturage.repository.ReservationRepository;
import ma.youcode.covoiturage.repository.TrajetRepository;
import ma.youcode.covoiturage.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PassagerRepository passagerRepository;
    private final TrajetRepository  trajetRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository, PassagerRepository passagerRepository, TrajetRepository trajetRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.passagerRepository = passagerRepository;
        this.trajetRepository = trajetRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public ReservationResponseDTO creerReservation(ReservationRequestDTO dto) {
        log.info("Création d'une réservation pour le passager ID {} sur le trajet ID {}", dto.passagerId(), dto.trajetId());
        Passager passager = passagerRepository.findById(dto.passagerId())
                .orElseThrow(()->new ResourceNotFoundException("Passager introuvable"));

        Trajet trajet = trajetRepository.findById(dto.trajetId())
                .orElseThrow(()->new ResourceNotFoundException("Trajet introuvable"));

        if(trajet.getStatut() != StatutTrajet.PUBLIE){
            log.warn("Réservation refusée : trajet {} non publié (statut={})", trajet.getId(), trajet.getStatut());
            throw new BusinessRuleException("Ce trajet n'est pas disponible pour les reservations");
        }

        if(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(dto.passagerId(), dto.trajetId(), StatutReservation.CONFIRMEE)){
            log.warn("Réservation refusée : réservation confirmée déjà existante pour passager {} sur trajet {}", dto.passagerId(), dto.trajetId());
            throw new BusinessRuleException("Ce passager possede deja une reservatoion confirmer pour ce trajet");
        }

        PointArret pointDepart = trajet.getPointsArret().stream().filter(point->point.getId().equals(dto.pointDepartId()))
                .findFirst().orElseThrow(()->new ResourceNotFoundException("Point de depart introuvable dans ce trajet"));

        PointArret pointArrivee = trajet.getPointsArret().stream()
                .filter(point -> point.getId().equals(dto.pointArriveeId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Point d'arrivee introuvable dans ce trajet"));

        if(pointDepart.getOrdre() >= pointArrivee.getOrdre()){
            log.warn("Réservation refusée : point de départ (ordre={}) >= point d'arrivée (ordre={})", pointDepart.getOrdre(), pointArrivee.getOrdre());
            throw new BadRequestException("le point de depart doit preceder le point d'arriver");
        }

        Long placesReservees = reservationRepository.compterPlacesReservees(trajet.getId(), StatutReservation.CONFIRMEE);

        long placesDisponibles = trajet.getPlacesProposees() - placesReservees;

        if(dto.nombrePlaces() > placesDisponibles){
            log.warn("Réservation refusée : places demandées ({}) > places disponibles ({})", dto.nombrePlaces(), placesDisponibles);
            throw new BusinessRuleException("Nombre de places insuffisant");
        }

        BigDecimal prixUnitaire = pointArrivee.getPrixDepuisDepart().subtract(pointDepart.getPrixDepuisDepart());

        BigDecimal montantTotal = prixUnitaire.multiply(BigDecimal.valueOf(dto.nombrePlaces()));

        Reservation reservation = new Reservation();
        reservation.setNombrePlaces(dto.nombrePlaces());
        reservation.setMontantTotal(montantTotal);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setPassager(passager);
        reservation.setTrajet(trajet);
        reservation.setPointDepart(pointDepart);
        reservation.setPointArrivee(pointArrivee);

        Reservation reservationEnregistre = reservationRepository.save(reservation);

        if(dto.nombrePlaces() == placesDisponibles){
            trajet.setStatut(StatutTrajet.COMPLET);
            trajetRepository.save(trajet);
            log.info("Trajet ID {} est devenu COMPLET", trajet.getId());
        }

        log.info("Réservation ID {} créée avec succès (Montant: {} DH)", reservationEnregistre.getId(), montantTotal);
        return reservationMapper.toResponseDTO(reservationEnregistre);

    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO trouverParId(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Reservation introuvable"));
        return reservationMapper.toResponseDTO(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> trouverParPassager(Long passagerId) {
        if(!passagerRepository.existsById(passagerId)){
            throw new ResourceNotFoundException("passager introuvable");
        }

        return reservationRepository.findByPassagerId(passagerId).stream()
                .map(reservationMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> trouverParConducteur(Long conducteurId) {
        return reservationRepository.findByTrajetConducteurId(conducteurId).stream()
                .map(reservationMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void annulerReservation(Long reservationId, Long passagerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()->new ResourceNotFoundException("reservationIntrouvable"));

        if(!reservation.getPassager().getId().equals(passagerId)){
            log.warn("Annulation refusée : passager ID {} n'est pas propriétaire de la réservation ID {}", passagerId, reservationId);
            throw new ForbiddenException("vous n'etes pas le proprietaire de cette reservation");
        }

        if(reservation.getStatut() != StatutReservation.CONFIRMEE){
            log.warn("Annulation refusée : réservation ID {} non confirmée (statut={})", reservationId, reservation.getStatut());
            throw new BusinessRuleException("Cette reservation ne paut pas etre annulee");
        }

        PointArret depart = reservation.getPointDepart();

        if(LocalDateTime.now().isAfter(depart.getHeurePassage().minusHours(24))){
            log.warn("Annulation refusée : délai de 24h dépassé pour la réservation ID {}", reservationId);
            throw new BusinessRuleException("l'annulation est possible uniquement jusqu'a 24 heures avant le depart");
        }

        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepository.save(reservation);

        Trajet trajet = reservation.getTrajet();

        if(trajet.getStatut() == StatutTrajet.COMPLET){
            trajet.setStatut(StatutTrajet.PUBLIE);
            trajetRepository.save(trajet);
            log.info("Trajet ID {} repasse au statut PUBLIE suite à annulation", trajet.getId());
        }

        log.info("Réservation ID {} annulée avec succès par le passager ID {}", reservationId, passagerId);
    }
}
