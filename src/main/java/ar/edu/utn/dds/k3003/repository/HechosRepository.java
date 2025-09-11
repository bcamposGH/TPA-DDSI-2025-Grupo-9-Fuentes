package ar.edu.utn.dds.k3003.repository;
import ar.edu.utn.dds.k3003.model.Hecho;
import java.util.List;
import java.util.Optional;

public interface HechosRepository{
    Optional<Hecho> findById(String id);
    Hecho save(Hecho hecho);
    List<Hecho> findAllByColeccionNombre(String coleccionNombre);
    List<Hecho> findAll();
}
