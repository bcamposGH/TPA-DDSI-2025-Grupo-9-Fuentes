package ar.edu.utn.dds.k3003.repository;

import ar.edu.utn.dds.k3003.model.Coleccion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class ColeccionRepositoryMem implements ColeccionRepository {

  private List<Coleccion> colecciones;

  public ColeccionRepositoryMem(){
    this.colecciones = new ArrayList<>();
  }

  public Optional<Coleccion> findById(String id) {
    return this.colecciones.stream().filter(x -> x.getNombre().equals(id)).findFirst();
  }

  public Coleccion save(Coleccion coleccion) {
    this.colecciones.add(coleccion);
    coleccion.setFechaModificacion(LocalDateTime.now());
    return coleccion;
  }

  public boolean existsById(String coleccionId) {
    return colecciones.stream().anyMatch(x -> x.getNombre().equals(coleccionId));
  }

  public List<Coleccion> findAll() {
    return new ArrayList<>(colecciones);
  }
}
