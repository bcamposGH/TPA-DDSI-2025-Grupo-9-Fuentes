package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Pdi {
    
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "hecho_id", referencedColumnName = "id")
    private Hecho hecho;
    private String descripcion;
    private String ubicacion;
    private LocalDateTime fecha;
    private String contenido;
    private List<String> etiquetas;

    public Pdi() {
        // Constructor por defecto requerido por JPA
    }

    public Pdi(String id, Hecho hecho, String descripcion, String ubicacion, LocalDateTime fecha, String contenido, List<String> etiquetas) {
        this.id = id;
        this.hecho = hecho;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.contenido = contenido;
        this.etiquetas = etiquetas;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Hecho getHecho() {
        return hecho;
    }

    public void setHecho(Hecho hecho) {
        this.hecho = hecho;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public List<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas;
    }
}
