package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
public class Hecho {

    @Id
    private String id;

    private String titulo;
    private String descripcion;
    private String contenido;

    @ElementCollection
    private List<String> etiquetas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CategoriaHechoEnum categoria;

    private String lugar;
    private LocalDateTime fecha;
    private LocalDateTime fechaCarga;
    private String origen;
    private String estado;

    @ManyToOne
    @JoinColumn(name = "coleccion_nombre", referencedColumnName = "nombre")
    private Coleccion coleccion;

    @ElementCollection
    private List<String> pdis = new ArrayList<>();

    protected Hecho() {
        this.estado = "activo";
        this.contenido = "";
    }

    public Hecho(String id, Coleccion coleccion, String titulo, List<String> etiquetas,
                 CategoriaHechoEnum categoria, String ubicacion, LocalDateTime fecha, String origen) {
        this.id = id;
        this.coleccion = coleccion;
        this.titulo = titulo;
        this.etiquetas = etiquetas != null ? etiquetas : new ArrayList<>();
        this.categoria = categoria;
        this.lugar = ubicacion;
        this.fechaCarga = fecha;
        this.origen = origen;
        this.estado = "activo";
        this.contenido = "";
    }

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