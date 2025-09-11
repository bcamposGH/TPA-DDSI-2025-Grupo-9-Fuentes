package ar.edu.utn.dds.k3003.app;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

@Service
public class ProcesadorPdiMock implements FachadaProcesadorPdI {
    
    @Override
    public PdIDTO procesar(PdIDTO pdIDTO) {
        // Implementación simple para pruebas
        return pdIDTO;
    }

    @Override
    public PdIDTO buscarPdIPorId(String pdiId) throws NoSuchElementException {
        // Simula la búsqueda de un PDI por ID, lanzando una excepción si no se encuentra
        if (pdiId == null || pdiId.isEmpty()) {
            throw new NoSuchElementException("PDI no encontrado con ID: " + pdiId);
        }
        // Retorna un PDI simulado
        return new PdIDTO(pdiId, "PDI Simulado", "Descripción del PDI Simulado", pdiId, null, pdiId, null);
    }

    @Override
    public List<PdIDTO> buscarPorHecho(String hechoId) throws NoSuchElementException {
        // Retorna una lista vacía para simular que no hay PDIs asociados al hecho
        return List.of();
    }

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
        // Método vacío, ya que no se necesita en el mock
    }    
}