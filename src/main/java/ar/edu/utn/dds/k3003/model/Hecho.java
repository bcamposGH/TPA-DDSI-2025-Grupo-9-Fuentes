package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import java.util.List;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import java.util.ArrayList;

@Data
@Entity
public class Hecho {

    public Hecho(String id, Coleccion coleccion, String titulo, List<String> etiquetas,
                 CategoriaHechoEnum categoria, String ubicacion, LocalDateTime fecha, String origen) {
        this.id = id;
        this.coleccion = coleccion;
        this.titulo = titulo;
        this.etiquetas = etiquetas;
        this.categoria = categoria;
        this.lugar = ubicacion;
        this.fechaCarga = fecha;
        this.origen = origen;
        this.estado = "activo"; // Estado por defecto
    }

    @Id
    private String id;
    // Id de la coleccion a la que pertenece el hecho
    private String titulo;
    private String descripcion;
    private String contenido; // Contenido del hecho, en esta primera iteracion solo texto
    private List<String> etiquetas;
    private CategoriaHechoEnum categoria;
    private String lugar;
    private LocalDateTime fecha;
    private LocalDateTime fechaCarga;
    private String origen;
    private String estado;
    @ManyToOne
    @JoinColumn(name = "coleccion_nombre", referencedColumnName = "nombre")
    private Coleccion coleccion;

    public Hecho() {
        // Constructor por defecto requerido por JPA
        this.estado = "activo"; // Estado por defecto
        this.etiquetas = new ArrayList<>();
        this.contenido = "";
    }

    private List<String> pdis = new ArrayList<>();

    public void agregarPdI(Pdi pdi) {
        this.pdis.add(pdi.getId());

    }

    public void censurar() {
        this.estado = "borrado";
    }

    public boolean estaCensurado() {
        return "borrado".equals(estado);
    }
}
