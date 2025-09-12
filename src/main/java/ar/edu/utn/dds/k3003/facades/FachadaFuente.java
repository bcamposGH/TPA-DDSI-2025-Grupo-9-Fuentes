package ar.edu.utn.dds.k3003.facades;

import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import java.util.List;
import java.util.NoSuchElementException;

public interface FachadaFuente {

  ColeccionDTO agregar(ColeccionDTO coleccionDTO);

  ColeccionDTO buscarColeccionXId(String coleccionId) throws NoSuchElementException;

  HechoDTO agregar(HechoDTO hechoDTO);
  HechoDTO buscarHechoXId(String hechoId) throws NoSuchElementException;

  List<HechoDTO> buscarHechosXColeccion(String coleccionId) throws NoSuchElementException;

  void setProcesadorPdI(FachadaProcesadorPdI procesador);

  PdIDTO agregar(PdIDTO pdIDTO) throws IllegalStateException;

  List<ColeccionDTO> colecciones();
}
