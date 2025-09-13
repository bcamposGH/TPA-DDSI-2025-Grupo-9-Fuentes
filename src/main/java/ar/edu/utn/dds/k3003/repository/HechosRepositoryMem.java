package ar.edu.utn.dds.k3003.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import ar.edu.utn.dds.k3003.model.Hecho;

@Repository
@Profile("test")
public class HechosRepositoryMem implements HechosRepository {

  private List<Hecho> hechos;


  public HechosRepositoryMem(){
    this.hechos = new ArrayList<>();
  }

  public List<Hecho> findAll() {
    return hechos;
  }

  public Hecho save(Hecho hecho) {
      this.hechos.add(hecho);
      hecho.setFechaCarga(LocalDateTime.now());
    return hecho;
  }

  public Optional<Hecho> findById(String id) {
    return this.hechos.stream().filter(h -> h.getId().equals(id)).findFirst();
  }
  public List<Hecho> findAllByColeccionNombre(String coleccionNombre) {
    return this.hechos.stream().filter(h -> h.getColeccion() != null && h.getColeccion().getNombre().equals(coleccionNombre)).toList();
  }

  @Override
  public void deleteAll() {
    hechos.clear();
  }
}
