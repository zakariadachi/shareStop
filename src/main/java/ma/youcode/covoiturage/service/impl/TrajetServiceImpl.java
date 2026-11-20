package ma.youcode.covoiturage.service.impl;

import lombok.extern.slf4j.Slf4j;
import ma.youcode.covoiturage.dto.*;
import ma.youcode.covoiturage.entity.*;
import ma.youcode.covoiturage.exception.BadRequestException;
import ma.youcode.covoiturage.exception.BusinessRuleException;
import ma.youcode.covoiturage.exception.ForbiddenException;
import ma.youcode.covoiturage.exception.ResourceNotFoundException;
import ma.youcode.covoiturage.mapper.PointArretMapper;
import ma.youcode.covoiturage.mapper.TrajetMapper;
import ma.youcode.covoiturage.repository.ConducteurRepository;
import ma.youcode.covoiturage.repository.ReservationRepository;
import ma.youcode.covoiturage.repository.TrajetRepository;
import ma.youcode.covoiturage.repository.VehiculeRepository;
import ma.youcode.covoiturage.service.TrajetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class TrajetServiceImpl implements TrajetService {

    private final TrajetRepository trajetRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReservationRepository reservationRepository;
    private final TrajetMapper trajetMapper;
    private final PointArretMapper pointArretMapper;

    public TrajetServiceImpl(
            TrajetRepository trajetRepository,
            ConducteurRepository conducteurRepository,
            VehiculeRepository vehiculeRepository,
            ReservationRepository reservationRepository,
            TrajetMapper trajetMapper,
            PointArretMapper pointArretMapper
    ) {
        this.trajetRepository = trajetRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.reservationRepository = reservationRepository;
        this.trajetMapper = trajetMapper;
        this.pointArretMapper = pointArretMapper;
    }

    @Override
    @Transactional
    public TrajetResponseDTO creerTrajet(TrajetRequestDTO dto) {
        log.info("Publication d'un nouveau trajet par le conducteur ID {}", dto.conducteurId());
        Conducteur conducteur = conducteurRepository.findById(dto.conducteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur introuvable"));

        Vehicule vehicule = vehiculeRepository.findById(dto.vehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule introuvable"));

        if (!vehicule.getConducteur().getId().equals(conducteur.getId())) {
            log.warn("Publication refusée : le véhicule ID {} n'appartient pas au conducteur ID {}", dto.vehiculeId(), dto.conducteurId());
            throw new ForbiddenException("Ce véhicule n'appartient pas à ce conducteur");
        }

        if (dto.placesProposees() > vehicule.getCapaciteMaximale()) {
            log.warn("Publication refusée : places proposées ({}) > capacité ({})", dto.placesProposees(), vehicule.getCapaciteMaximale());
            throw new BadRequestException("Le nombre de places proposé dépasse la capacité du véhicule");
        }

        List<PointArretRequestDTO> points = dto.pointsArret();

        if (points == null || points.size() < 2) {
            log.warn("Publication refusée : moins de 2 points d'arrêt");
            throw new BadRequestException("Un trajet doit contenir au moins deux points d'arrêt");
        }

        List<PointArretRequestDTO> pointsTries = points.stream().sorted(Comparator.comparingInt(PointArretRequestDTO::ordre)).toList();

        Set<String> villes = new HashSet<>();

        LocalDateTime heurePrecedente = null;

        BigDecimal prixPrecedent = null;

        for (int i = 0; i < pointsTries.size(); i++){
            PointArretRequestDTO point = pointsTries.get(i);

            if(point.ordre() != i){
                throw  new BusinessRuleException("L'ordre des points d'arret doit a 0 et etre continu");
            }

            String villeNormalisee = point.ville().trim().toLowerCase(Locale.ROOT);

            if(!villes.add(villeNormalisee)){
                throw new BusinessRuleException("une ville ne peut apparaitre qu'une seule fois dans un trajet");
            }

            if(i==0){
                if(point.heurePassage().isBefore(LocalDateTime.now()) || point.heurePassage().equals(LocalDateTime.now())){
                    throw new BusinessRuleException("la date de depart doit etre dans le futur");
                }

                if(point.prixDepuisDepart().compareTo(BigDecimal.ZERO) != 0){
                    throw new BusinessRuleException("Le prix du premier point d'arret doit etre egale a 0");
                }
            }else{
                if(!point.heurePassage().isAfter(heurePrecedente)){
                    throw new BusinessRuleException("Les horaires des points d'arrêt doivent être croissants");
                }

                if (point.prixDepuisDepart().compareTo(prixPrecedent) <= 0) {
                    throw new BusinessRuleException("Les prix des points d'arrêt doivent être strictement croissants");
                }
            }
            heurePrecedente = point.heurePassage();
            prixPrecedent = point.prixDepuisDepart();
        }

        Trajet trajet = trajetMapper.toEntity(dto);

        trajet.setConducteur(conducteur);
        trajet.setVehicule(vehicule);
        trajet.setStatut(StatutTrajet.PUBLIE);

        for(PointArretRequestDTO pointDto : pointsTries){
            PointArret pointArret = pointArretMapper.toEntity(pointDto);

            pointArret.setTrajet(trajet);
            trajet.getPointsArret().add(pointArret);
        }

        Trajet trajetEnregistre = trajetRepository.save(trajet);

        Long placesRestantes = trajetEnregistre.getPlacesProposees() != null
                ? Long.valueOf(trajetEnregistre.getPlacesProposees())
                : (dto.placesProposees() != null ? Long.valueOf(dto.placesProposees()) : 0L);
        return trajetMapper.toResponseDTO(trajetEnregistre, placesRestantes);
    }

    @Override
    @Transactional(readOnly = true)
    public TrajetResponseDTO trouverParId(Long id) {
        Trajet trajet = trajetRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable"));
        Long placesReservees = reservationRepository.compterPlacesReservees(id, StatutReservation.CONFIRMEE);
        Long placesProposees = trajet.getPlacesProposees() != null ? Long.valueOf(trajet.getPlacesProposees()) : 0L;
        Long placesRestantes = placesProposees - placesReservees;
        return trajetMapper.toResponseDTO(trajet, placesRestantes);
    }

    @Override
    @Transactional
    public void terminerTrajet(Long trajetId, Long conducteurId) {
        Trajet trajet = trajetRepository.findById(trajetId)
                .orElseThrow(()-> new ResourceNotFoundException("Trajet introuvable"));

        if(!trajet.getConducteur().getId().equals(conducteurId)){
            throw new ForbiddenException("vous n'etes pas proprietaire de ce trajet");
        }

        if(trajet.getStatut() != StatutTrajet.COMPLET  && trajet.getStatut() != StatutTrajet.PUBLIE){
            throw new BusinessRuleException("ce trajet ne peut pas etre terminer");
        }

        PointArret depart = trajet.getPointsArret().get(0);

        if(LocalDateTime.now().isBefore(depart.getHeurePassage())){
            throw new BusinessRuleException("Impossible de terminer un trajet avant son depart");
        }

        trajet.setStatut(StatutTrajet.TERMINE);
        trajetRepository.save(trajet);
    }

    @Override
    @Transactional
    public void annulerTrajet(Long trajetId, Long conducteurId) {
        Trajet trajet = trajetRepository.findById(trajetId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable"));

        if(!trajet.getConducteur().getId().equals(conducteurId)){
            throw new ForbiddenException("vous n'etes pas proprietaire de ce trajet");
        }

        if(trajet.getStatut() != StatutTrajet.PUBLIE && trajet.getStatut() != StatutTrajet.COMPLET){
            throw new BusinessRuleException("ce trajet ne peut pas etre annuler");
        }

        trajet.setStatut(StatutTrajet.ANNULE);
        trajetRepository.save(trajet);

        List<Reservation> reservations = reservationRepository.findByTrajetIdAndStatut(trajetId, StatutReservation.CONFIRMEE);
        for (Reservation res : reservations) {
            res.setStatut(StatutReservation.ANNULEE);
        }
        reservationRepository.saveAll(reservations);
        log.info("Trajet ID {} et ses {} réservation(s) confirmée(s) annulés en bloc", trajetId, reservations.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrajetSearchProjection> rechercherTrajets(TrajetSearchRequestDTO dto, Pageable pageable) {
        LocalDateTime debutJournee = dto.date().atStartOfDay();

        LocalDateTime finJournee = dto.date().plusDays(1).atStartOfDay();

        return trajetRepository.rechercherTrajets(
                dto.villeDepart(),
                dto.villeArrivee(),
                debutJournee,
                finJournee,
                dto.nombrePlaces(),
                dto.prixMax(),
                StatutTrajet.PUBLIE,
                StatutReservation.CONFIRMEE,
                pageable
        );
    }
}
