package ar.edu.utn.dds.k3003.clients;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HechosSinSolicitudesResponse(@JsonProperty("hechosSinSolicitudes") List<String> hechosSinSolicitudes) {}