package ma.youcode.covoiturage.service.impl;


import ma.youcode.covoiturage.dto.ConducteurRequestDTO;
import ma.youcode.covoiturage.dto.ConducteurResponseDTO;
import ma.youcode.covoiturage.entity.Conducteur;
import ma.youcode.covoiturage.mapper.ConducteurMapper;
import ma.youcode.covoiturage.repository.ConducteurRepository;
import ma.youcode.covoiturage.service.ConducteurService;
import org.springframework.stereotype.Service;

@Service
public class ConducteurServiceImpl implements ConducteurService {

    private final ConducteurRepository conducteurRepository;
    private  final ConducteurMapper conducteurMapper;

    public ConducteurServiceImpl(
            ConducteurRepository conducteurRepository,
            ConducteurMapper conducteurMapper
    ){
        this.conducteurRepository = conducteurRepository;
        this.conducteurMapper = conducteurMapper;
    }

    @Override
    public ConducteurResponseDTO creerConducteur(ConducteurRequestDTO dto){
        Conducteur conducteur = conducteurMapper.toEntity(dto);

        Conducteur conducteurEnregistre = conducteurRepository.save(conducteur);

        return  conducteurMapper.toResponseDTO(conducteurEnregistre);
    }

    @Override
    public ConducteurResponseDTO findById(Long id) {
        Conducteur conducteur = conducteurRepository.findById(id).orElseThrow(()->new RuntimeException("Conducteur introuvable"));
        return conducteurMapper.toResponseDTO(conducteur);
    }
}
