package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.Coleccion;
import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.repository.HechosRepository;
import ar.edu.utn.dds.k3003.repository.PdIRepository;
import ar.edu.utn.dds.k3003.repository.ColeccionRepository;
import ar.edu.utn.dds.k3003.repository.ColeccionRepositoryMem;
import ar.edu.utn.dds.k3003.repository.HechosRepositoryMem;
import ar.edu.utn.dds.k3003.repository.PdIRepositoryMem;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.val;

@Service
public class Fachada implements FachadaFuente {

    // Repositorios JPA
    private ColeccionRepository coleccionRepository;
    private HechosRepository hechosRepository;
    private FachadaProcesadorPdI procesadorPdI;
    protected Fachada() {
    this.coleccionRepository = new ColeccionRepositoryMem();
    this.hechosRepository = new HechosRepositoryMem();
    new PdIRepositoryMem();
  }

  @Autowired
  public Fachada(
      ColeccionRepository coleccionRepository,
      HechosRepository hechosRepository,
      @Autowired(required = false) FachadaProcesadorPdI procesadorPdI,
      PdIRepository pdiRepository
      ){
        this.coleccionRepository = coleccionRepository;
        this.hechosRepository = hechosRepository;
        this.procesadorPdI = procesadorPdI;
      }

  @Override
  public ColeccionDTO agregar(ColeccionDTO coleccionDTO) {
    if (this.coleccionRepository.findById(coleccionDTO.nombre()).isPresent()){
      throw new IllegalArgumentException(coleccionDTO.nombre() + " ya existe");
    }
    val coleccion = new Coleccion(coleccionDTO.nombre(), coleccionDTO.descripcion(), null);
    coleccion.setFechaModificacion(java.time.LocalDateTime.now());
    this.coleccionRepository.save(coleccion);
    return new ColeccionDTO(coleccion.getNombre(), coleccion.getDescripcion());
  }

  @Override
  public ColeccionDTO buscarColeccionXId(String coleccionId) throws NoSuchElementException {
    val coleccionOptional = this.coleccionRepository.findById(coleccionId);
    if(coleccionOptional.isEmpty()){
      throw  new NoSuchElementException(coleccionId + " no existe");
    }
    val coleccion = coleccionOptional.get();
    return new ColeccionDTO(coleccion.getNombre(),coleccion.getDescripcion());
  }

  @Override
  @Transactional
  public HechoDTO agregar(HechoDTO hechoDTO) {

    Hecho hecho = new Hecho(
        java.util.UUID.randomUUID().toString(),
        this.coleccionRepository.findById(hechoDTO.nombreColeccion())
            .orElseThrow(() -> new NoSuchElementException("Colección no encontrada: " + hechoDTO.nombreColeccion())),
        hechoDTO.titulo(),
        hechoDTO.etiquetas(),
        hechoDTO.categoria(),
        hechoDTO.ubicacion(),
        hechoDTO.fecha(),
        hechoDTO.origen()
    );
    // Guardar el hecho en el repositorio

    this.hechosRepository.save(hecho);

    return new HechoDTO(
        hecho.getId(),
        hecho.getColeccion().getNombre(),
        hecho.getTitulo(),
        hecho.getEtiquetas(),
        hecho.getCategoria(),
        hecho.getLugar(),
        hecho.getFecha(),
        hecho.getOrigen()
    );
  }

  @Override
  public HechoDTO buscarHechoXId(String hechoId) throws NoSuchElementException {
    Hecho hecho = this.hechosRepository.findById(hechoId)
    .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado: " + hechoId));

    if (hecho.estaCensurado()) {
      throw new NoSuchElementException("Hecho censurado: " + hechoId);
    }
    return convertirHechoADTO(hecho);
  }

  @Override
  public List<HechoDTO> buscarHechosXColeccion(String coleccionNombre) throws NoSuchElementException {
    val hechos = this.hechosRepository.findAllByColeccionNombre(coleccionNombre);
    if (hechos.isEmpty()) {
      throw new NoSuchElementException("No se encontraron hechos para la colección: " + coleccionNombre);
    }
    return hechos.stream()
      .filter(hecho -> !hecho.estaCensurado())
      .map(this::convertirHechoADTO)
      .collect(Collectors.toList());
  }

  @Override
  public void setProcesadorPdI(FachadaProcesadorPdI procesador) {
    this.procesadorPdI = procesador;
  }

  @Override
  public PdIDTO agregar(PdIDTO pdIDTO) throws IllegalStateException {
      // 1. Procesar en el servicio externo
      PdIDTO pdiProcesada = procesadorPdI.procesar(pdIDTO);

      // 2. Buscar el hecho
      Hecho hecho = hechosRepository.findById(pdIDTO.hechoId())
          .orElseThrow(() -> new NoSuchElementException("No existe el hecho con ID: " + pdIDTO.hechoId()));

      // 3. Guardar solo el ID de la PdI procesada
      hecho.agregarPdI(pdiProcesada.id());
      hechosRepository.save(hecho);

      // 4. Retornar la PdI procesada (para que el caller tenga la info)
      return pdiProcesada;
  }


  public void censurar(String hechoId) {
    Hecho hecho = hechosRepository.findById(hechoId)
    .orElseThrow(() -> new NoSuchElementException("No existe el hecho con ID: " + hechoId));

    hecho.censurar();
    hechosRepository.save(hecho);
  }

  public List<HechoDTO> hechos() {
    return hechosRepository.findAll().stream()
        .filter(hecho -> !hecho.estaCensurado()) // Filtramos los censurados
        .map(this::convertirHechoADTO)
        .collect(Collectors.toList());
  }

  // Método helper para conversión
  private HechoDTO convertirHechoADTO(Hecho hecho) {
    return new HechoDTO(
        hecho.getId(),
        hecho.getColeccion().getNombre(),
        hecho.getTitulo(),
        hecho.getEtiquetas(),
        hecho.getCategoria(),
        hecho.getLugar(),
        hecho.getFecha(),
        hecho.getOrigen()
    );
  }

  @Override
  public List<ColeccionDTO> colecciones() {
    return this.coleccionRepository.findAll().stream()
        .map(coleccion -> new ColeccionDTO(coleccion.getNombre(), coleccion.getDescripcion()))
        .toList();
  }


public List<PdIDTO> buscarPdIsPorHecho(String hechoId) {
    // 1. Buscar el hecho
    Hecho hecho = hechosRepository.findById(hechoId)
        .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado: " + hechoId));

    // 2. Para cada ID, consultar al ProcesadorPdI
    return hecho.getPdiIds().stream()
        .map(pdiId -> {
            try {
                return procesadorPdI.buscarPdIPorId(pdiId); // 👈 este método hay que agregarlo en el proxy
            } catch (Exception e) {
                throw new RuntimeException("Error al buscar PdI " + pdiId, e);
            }
        })
        .toList();
      }

    }