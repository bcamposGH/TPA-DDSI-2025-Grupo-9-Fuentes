package ar.edu.utn.dds.k3003.facades.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PdIDTO(
        String id,
        @JsonAlias("hecho_id") String hechoId,
        String descripcion,
        String lugar,
        LocalDateTime momento,
        @JsonAlias("url_imagen") String urlImagen,
        @JsonAlias("texto_imagen") String textoImagen,
        List<String> etiquetas
) {}