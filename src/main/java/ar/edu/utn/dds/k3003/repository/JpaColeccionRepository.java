package ar.edu.utn.dds.k3003.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ar.edu.utn.dds.k3003.model.Coleccion;

@Repository
@Profile("!test")
public interface JpaColeccionRepository extends ColeccionRepository, JpaRepository<Coleccion, String> {

}