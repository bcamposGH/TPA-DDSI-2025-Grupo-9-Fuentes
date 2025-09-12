package ar.edu.utn.dds.k3003.facades.dtos;

public record SolicitudDTO(String id, String descripcion, EstadoSolicitudBorradoEnum estado,
                           String hechoId) {

}
