package ar.edu.utn.dds.k3003.facades.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record PdIDTO(String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas) {
   public PdIDTO(String id, String hechoId) {
      this(id, hechoId, (String)null, (String)null, (LocalDateTime)null, (String)null, List.of());
   }

   public PdIDTO(String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas) {
      this.id = id;
      this.hechoId = hechoId;
      this.descripcion = descripcion;
      this.lugar = lugar;
      this.momento = momento;
      this.contenido = contenido;
      this.etiquetas = etiquetas;
   }

   public String id() {
      return this.id;
   }

   public String hechoId() {
      return this.hechoId;
   }

   public String descripcion() {
      return this.descripcion;
   }

   public String lugar() {
      return this.lugar;
   }

   public LocalDateTime momento() {
      return this.momento;
   }

   public String contenido() {
      return this.contenido;
   }

   public List<String> etiquetas() {
      return this.etiquetas;
   }
}
