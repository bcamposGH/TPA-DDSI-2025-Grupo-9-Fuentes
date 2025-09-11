package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Coleccion {
  
  @Id
  private String nombre;
  
  private String descripcion;
  private LocalDateTime fechaModificacion;

  public Coleccion() {
    // Constructor por defecto requerido por JPA
  }

  public Coleccion(String nombre, String descripcion) {
    this.nombre = nombre;
    this.descripcion = descripcion;
  }

  //Getters y Setters
  public LocalDateTime getFechaModificacion() {
    return fechaModificacion;
  }
  public void setFechaModificacion(LocalDateTime fechaModificacion) {
    this.fechaModificacion = fechaModificacion;
  }
  public String getNombre() {
    return nombre;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  public String getDescripcion() {
    return descripcion;
  }
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
}
