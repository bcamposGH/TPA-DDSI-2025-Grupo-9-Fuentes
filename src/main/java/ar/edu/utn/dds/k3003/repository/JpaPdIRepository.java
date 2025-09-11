package ar.edu.utn.dds.k3003.repository;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.dds.k3003.model.Pdi;

@Repository
@Profile("!test")
public interface JpaPdIRepository extends PdIRepository, JpaRepository<Pdi, String> {

    List<Pdi> findAllByHechoId(String hechoId);

}
