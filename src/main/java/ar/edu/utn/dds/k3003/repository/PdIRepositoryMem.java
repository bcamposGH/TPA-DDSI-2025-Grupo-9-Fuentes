package ar.edu.utn.dds.k3003.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import ar.edu.utn.dds.k3003.model.Pdi;
import java.util.ArrayList;

@Repository
@Profile("test")
public class PdIRepositoryMem implements PdIRepository {

    private List<Pdi> pdiList;

    public PdIRepositoryMem() {
        this.pdiList = new ArrayList<>();
    }
    public Pdi save(Pdi pdi) {
        pdiList.add(pdi);
        return pdi;
    }
    public Optional<Pdi> findById(String id) {
        return pdiList.stream().filter(pdi -> pdi.getId().equals(id)).findFirst();
    }
    public List<Pdi> findAllByHechoId(String hechoId) {
        return pdiList.stream().filter(pdi -> pdi.getHecho().getId().equals(hechoId))
                      .collect(Collectors.toList());
    }
    public List<Pdi> findAll() {
        // Implementation for getting all Pdis
        return new ArrayList<>(pdiList);
    }

}
