package com.tecabix.bz.autorizacion;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.tecabix.db.entity.Usuario;
import com.tecabix.db.repository.UsuarioRepository;
import com.tecabix.res.b.RSB001;
import com.tecabix.sv.rq.RQSV001;

/**
 * Implementación del negocio de autorización.
 * <p>
 * Esta clase se encarga de procesar las peticiones de autorización,
 * obteniendo las autorizaciones del usuario a partir de la información
 * proporcionada en la solicitud.
 * </p>
 *
 * @author Ramirez Urrutia Angel Abinadi
 */
public class Autorizacion001BZ {

    /**
     * Repositorio de usuarios.
     * <p>
     * Se utiliza para acceder a la información de los usuarios almacenada en la
     * base de datos.
     * </p>
     */
    private UsuarioRepository usuarioRepository;

    /**
     * Mensaje cuando no se encuentra el usuario.
     */
    private static final String NO_SE_ENCONTRO_EL_USUARIO;

    static {
        NO_SE_ENCONTRO_EL_USUARIO = "No se encontro el usuario.";
    }

    /**
     * Constructor de la clase {@code Autorizacion001BZ}.
     *
     * Inicializa una nueva instancia de {@code Autorizacion001BZ} con el
     * repositorio de usuarios proporcionado. Este repositorio se utiliza para
     * acceder a los datos relacionados con los usuarios dentro del contexto de
     * autorización.
     *
     * @param repository el repositorio de usuarios.
     */
    public Autorizacion001BZ(final UsuarioRepository repository) {
        this.usuarioRepository = repository;
    }

    /**
     * Obtiene las autorizaciones del usuario a partir de la solicitud
     * proporcionada.
     * <p>
     * El método extrae el nombre del usuario de la sesión incluida en el objeto
     * de solicitud, consulta el repositorio para verificar la existencia del
     * usuario y, en caso afirmativo, retorna las autorizaciones asociadas al
     * perfil del usuario. Si el usuario no es encontrado, se retorna una
     * respuesta de error indicando que no se encontró el usuario.
     * </p>
     *
     * @param rqsv001 objeto que contiene la sesión y la estructura de respuesta
     *                inicial.
     * @return ResponseEntity que contiene el objeto {@code RSB001} con el
     *         resultado de la operación, ya sea con las autorizaciones del
     *         usuario o con un mensaje de error en caso de que el usuario no
     *         exista.
     */
    public ResponseEntity<RSB001> obtener(final RQSV001 rqsv001) {

        final RSB001 rsb001;
        final String nombre;
        final Optional<Usuario> optional;

        rsb001 = rqsv001.getRsb001();
        nombre = rqsv001.getSesion().getUsuario().getNombre();
        optional = usuarioRepository.findByNombre(nombre);
        if (!optional.isPresent()) {
            return rsb001.badRequest(NO_SE_ENCONTRO_EL_USUARIO);
        }
        final Usuario usuario = optional.get();
        return rsb001.ok(usuario.getPerfil().getAutorizaciones());
    }
}
