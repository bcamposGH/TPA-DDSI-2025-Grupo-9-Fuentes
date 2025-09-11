package ar.edu.utn.dds.k3003.repository;
import ar.edu.utn.dds.k3003.model.Pdi;
import java.util.List;
import java.util.Optional;

public interface PdIRepository{
    Optional<Pdi> findById(String id);
    Pdi save(Pdi pdi);
    List<Pdi> findAllByHechoId(String hechoId);
}
