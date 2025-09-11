package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class FachadaTest {

    @Autowired
    private Fachada fachada;
    private FachadaProcesadorPdI mockProcesador;

    @BeforeEach
    void setUp() {
        mockProcesador = Mockito.mock(FachadaProcesadorPdI.class);
        fachada.setProcesadorPdI(mockProcesador);
    }

    @Test
    void testAgregarColeccion() {
        ColeccionDTO coleccionDTO = new ColeccionDTO("test1", "Descripción test");
        
        ColeccionDTO resultado = fachada.agregar(coleccionDTO);
        
        assertEquals("test1", resultado.nombre());
        assertEquals("Descripción test", resultado.descripcion());
    }

    @Test
    void testAgregarColeccionExistente() {
        ColeccionDTO coleccionDTO = new ColeccionDTO("test1", "Descripción test");
        fachada.agregar(coleccionDTO);
        
        assertThrows(IllegalArgumentException.class, () -> {
            fachada.agregar(coleccionDTO);
        });
    }

    @Test
    void testBuscarColeccionXId() {
        ColeccionDTO coleccionDTO = new ColeccionDTO("test1", "Descripción test");
        fachada.agregar(coleccionDTO);
        
        ColeccionDTO resultado = fachada.buscarColeccionXId("test1");
        
        assertEquals("test1", resultado.nombre());
    }

    @Test
    void testBuscarColeccionXIdNoExistente() {
        assertThrows(NoSuchElementException.class, () -> {
            fachada.buscarColeccionXId("no-existe");
        });
    }

    @Test
    void testAgregarHecho() {
        fachada.agregar(new ColeccionDTO("col1", "Colección 1"));
        HechoDTO hechoDTO = new HechoDTO(
            "", "col1", "Título hecho", 
            List.of("etiqueta1"), CategoriaHechoEnum.ENTRETENIMIENTO, 
            "BsAs", LocalDateTime.now(), "origen1");
        
        HechoDTO resultado = fachada.agregar(hechoDTO);
        
        assertNotNull(resultado.id());
        assertEquals("Título hecho", resultado.titulo());
    }

    @Test
    void testBuscarHechoXId() {
        fachada.agregar(new ColeccionDTO("col1", "Colección 1"));
        HechoDTO hechoDTO = new HechoDTO(
            "", "col1", "Título hecho", 
            List.of("etiqueta1"), CategoriaHechoEnum.ENTRETENIMIENTO, 
            "BsAs", LocalDateTime.now(), "origen1");
        HechoDTO creado = fachada.agregar(hechoDTO);
        
        HechoDTO resultado = fachada.buscarHechoXId(creado.id());
        
        assertEquals(creado.id(), resultado.id());
    }

    @Test
    void testBuscarHechoXIdNoExistente() {
        assertThrows(NoSuchElementException.class, () -> {
            fachada.buscarHechoXId("no-existe");
        });
    }

    @Test
    void testBuscarHechosXColeccion() {
    // Configuración
    fachada.agregar(new ColeccionDTO("col1", "Colección 1"));
    fachada.agregar(new HechoDTO(
        "", "col1", "Hecho 1", List.of(), null, null, null, null));
    fachada.agregar(new HechoDTO(
        "", "col1", "Hecho 2", List.of(), null, null, null, null));
    
    // Ejecución
    List<HechoDTO> resultados = fachada.buscarHechosXColeccion("col1");
    
    // Verificación
    assertEquals(2, resultados.size(), "Deberían retornarse 2 hechos");
    assertTrue(resultados.stream().anyMatch(h -> h.titulo().equals("Hecho 1")));
    assertTrue(resultados.stream().anyMatch(h -> h.titulo().equals("Hecho 2")));
    }
    
    @Test
    void testAgregarPdI() {
        fachada.agregar(new ColeccionDTO("col1", "Colección 1"));
        HechoDTO hecho = fachada.agregar(new HechoDTO(
            "", "col1", "Hecho 1", List.of(), null, null, null, null));
        
        when(mockProcesador.procesar(any(PdIDTO.class)))
            .thenReturn(new PdIDTO("pdi1", hecho.id()));
        
        PdIDTO pdiDTO = new PdIDTO("", hecho.id(), "Desc", "Lugar", LocalDateTime.now(), "Cont", List.of());
        PdIDTO resultado = fachada.agregar(pdiDTO);
        
        assertNotNull(resultado.id());
    }

    @Test
    void testCensurarHecho() {
        fachada.agregar(new ColeccionDTO("col1", "Colección 1"));
        HechoDTO hecho = fachada.agregar(new HechoDTO(
            "", "col1", "Hecho 1", List.of(), null, null, null, null));
        
        fachada.censurar(hecho.id());
        
        assertThrows(NoSuchElementException.class, () -> {
            fachada.buscarHechoXId(hecho.id());
        });
    }

    @Test
    void testHechosNoIncluyeCensurados() {
        fachada.agregar(new ColeccionDTO("col1", "Colección 1"));
        HechoDTO hecho1 = fachada.agregar(new HechoDTO(
            "", "col1", "Hecho 1", List.of(), null, null, null, null));
        HechoDTO hecho2 = fachada.agregar(new HechoDTO(
            "", "col1", "Hecho 2", List.of(), null, null, null, null));
        fachada.censurar(hecho2.id());
        
        List<HechoDTO> resultados = fachada.hechos();
        
        assertEquals(1, resultados.size());
        assertEquals(hecho1.id(), resultados.get(0).id());
    }
}