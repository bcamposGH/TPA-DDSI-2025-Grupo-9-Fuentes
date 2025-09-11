package ar.edu.utn.dds.k3003.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.context.annotation.Profile;

import ar.edu.utn.dds.k3003.model.Hecho;

@Repository
@Profile("!test")
public interface JpaHechosRepository extends HechosRepository, JpaRepository<Hecho, String> {

    List<Hecho> findAllByColeccionNombre(String coleccionNombre);

}