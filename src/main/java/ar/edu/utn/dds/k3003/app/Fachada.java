package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.SolicitudesProxy;
import ar.edu.utn.dds.k3003.config.Metricas;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.Coleccion;
import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.repository.HechosRepository;
import ar.edu.utn.dds.k3003.repository.ColeccionRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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
    @Autowired
    private Metricas metrics;
    @Autowired
    private SolicitudesProxy solicitudesProxy;

  @Autowired
  public Fachada(
      ColeccionRepository coleccionRepository,
      HechosRepository hechosRepository,
      @Autowired(required = false) FachadaProcesadorPdI procesadorPdI
      ){
        this.coleccionRepository = coleccionRepository;
        this.hechosRepository = hechosRepository;
        this.procesadorPdI = procesadorPdI;
      }

  @Transactional
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
    metrics.registrarHechoCreado();

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
@Transactional
public PdIDTO agregar(PdIDTO pdIDTO) throws IllegalStateException {
    // 1. Validar que exista el hecho
    Hecho hecho = hechosRepository.findById(pdIDTO.hechoId())
        .orElseThrow(() -> new NoSuchElementException("No existe el hecho con ID: " + pdIDTO.hechoId()));

    // 2. Generar el id en la Fuente
    String nuevoId = UUID.randomUUID().toString();
    PdIDTO dtoConId = new PdIDTO(
        nuevoId,
        pdIDTO.hechoId(),
        pdIDTO.descripcion(),
        pdIDTO.lugar(),
        pdIDTO.momento(),
        pdIDTO.urlImagen(),
        pdIDTO.textoImagen(),
        pdIDTO.etiquetas()
    );

    // 3. Enviar al Procesador para que valide/persista
    PdIDTO procesada = procesadorPdI.procesar(dtoConId);
    if (procesada == null) {
        throw new IllegalStateException("La PdI no es válida");
    }

    // 4. Guardar el id en el Hecho
    hecho.agregarPdI(procesada.id());
    hechosRepository.save(hecho);

    // 5. Retornar la PdI procesada
    return procesada;
}


  public void censurar(String hechoId) {
    Hecho hecho = hechosRepository.findById(hechoId)
    .orElseThrow(() -> new NoSuchElementException("No existe el hecho con ID: " + hechoId));

    hecho.censurar();
    metrics.registrarHechoCensurado();
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
    // 1. Validar que exista el hecho en esta fuente
    hechosRepository.findById(hechoId)
        .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado: " + hechoId));

    // 2. Delegar directamente al ProcesadorPdI (proxy)
    return procesadorPdI.buscarPorHecho(hechoId);
  }

  public List<PdIDTO> obtenerTodosLosPdIs() {
    return procesadorPdI.obtenerTodos();
  }

  public List<HechoDTO> hechosSinSolicitudesPorColeccion(String coleccionId) {
    // 1. Traer todos los hechos de la colección
    List<HechoDTO> hechos = this.buscarHechosXColeccion(coleccionId);

    // 2. Armar lista de IDs
    List<String> ids = hechos.stream()
            .map(HechoDTO::id)
            .toList();

    if (ids.isEmpty()) return List.of();

    // 3. Consultar a Solicitudes
    List<String> idsSinSolicitudes = solicitudesProxy.hechosSinSolicitudes(ids);

    // 4. Filtrar hechos de la colección
    return hechos.stream()
            .filter(h -> idsSinSolicitudes.contains(h.id()))
            .toList();
 }
}