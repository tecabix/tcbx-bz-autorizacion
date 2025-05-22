package com.tecabix.bz.autorizacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.tecabix.db.entity.Autorizacion;
import com.tecabix.db.entity.Perfil;
import com.tecabix.db.entity.Sesion;
import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.UsuarioRepository;
import com.tecabix.res.b.RSB001;
import com.tecabix.sv.rq.RQSV001;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para la clase Autorizacion001BZ. Verifica los
 * casos de éxito y error al obtener autorizaciones de un usuario.
 */
@ExtendWith(MockitoExtension.class)
class Autorizacion001BZTest {

    /**
     * Repositorio simulado para operaciones con la entidad Usuario.
     */
    @Mock
    private UsuarioRepository usuarioRepository;

    /**
     * Objeto de respuesta simulado para validar respuestas del servicio.
     */
    @Mock
    private RSB001 rsb001;

    /**
     * Objeto Perfil con inyección de dependencias para pruebas.
     */
    @InjectMocks
    private Perfil perfil;

    /**
     * Objeto Usuario con inyección de dependencias para pruebas.
     */
    @InjectMocks
    private Usuario usuario;

    /**
     * Objeto Sesion con inyección de dependencias para pruebas.
     */
    @InjectMocks
    private Sesion sesion;

    /**
     * Instancia de la clase bajo prueba, construida con mocks.
     */
    private Autorizacion001BZ autorizacion;

    /**
     * Inicializa la instancia antes de cada caso de prueba.
     */
    @BeforeEach
    void setUp() {
        autorizacion = new Autorizacion001BZ(usuarioRepository);
    }

    /**
     * Prueba el caso en que el usuario es encontrado y se devuelve
     * la lista de autorizaciones asociadas exitosamente.
     */
    @Test
    void encontrarUsuario() {
        final String nombreUsuario = "juan";
        final List<Autorizacion> autorizaciones = new ArrayList<>();

        perfil.setAutorizaciones(autorizaciones);
        usuario.setNombre(nombreUsuario);
        usuario.setPerfil(perfil);
        sesion.setUsuario(usuario);

        RQSV001 rqsv001 = new RQSV001(rsb001);
        rqsv001.setSesion(sesion);

        when(usuarioRepository.findByNombre(nombreUsuario))
            .thenReturn(Optional.of(usuario));
        when(rsb001.ok(autorizaciones))
            .thenReturn(ResponseEntity.ok(rsb001));

        ResponseEntity<RSB001> response = autorizacion.obtener(rqsv001);

        int ok = HttpStatus.OK.value();
        assertEquals(ok, response.getStatusCodeValue());
        verify(usuarioRepository).findByNombre(nombreUsuario);
        verify(rsb001).ok(autorizaciones);
    }

    /**
     * Prueba el caso en que no se encuentra el usuario. El método debe
     * responder con un código de error BAD_REQUEST.
     */
    @Test
    void noEncontrarUsuario() {
        final String nombreUsuario = "no_existe";

        usuario.setNombre(nombreUsuario);
        sesion.setUsuario(usuario);

        RQSV001 rqsv001 = new RQSV001(rsb001);
        rqsv001.setSesion(sesion);

        when(usuarioRepository.findByNombre(nombreUsuario))
            .thenReturn(Optional.empty());
        when(rsb001.badRequest("No se encontro el usuario."))
            .thenReturn(ResponseEntity.badRequest().body(rsb001));

        ResponseEntity<RSB001> response = autorizacion.obtener(rqsv001);

        int badRequest = HttpStatus.BAD_REQUEST.value();
        assertEquals(badRequest, response.getStatusCodeValue());
        verify(usuarioRepository).findByNombre(nombreUsuario);
        verify(rsb001).badRequest("No se encontro el usuario.");
    }
}
