package cl.duoc.vidasalud.appointments.service;

import cl.duoc.vidasalud.appointments.dto.AtencionDtos.CrearAtencionRequest;
import cl.duoc.vidasalud.appointments.entity.Atencion;
import cl.duoc.vidasalud.appointments.entity.EstadoAtencion;
import cl.duoc.vidasalud.appointments.exception.AtencionNoEncontradaException;
import cl.duoc.vidasalud.appointments.exception.TransicionInvalidaException;
import cl.duoc.vidasalud.appointments.messaging.PublicadorEventos;
import cl.duoc.vidasalud.appointments.repository.AtencionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AtencionService {

    /**
     * Transiciones permitidas. Refleja literalmente el flujo del caso:
     * SOLICITADA -> CONFIRMADA -> EN_ESPERA -> EN_ATENCION -> CERRADA
     * con CANCELADA disponible mientras la atención sigue activa.
     *
     * La regla explícita del enunciado ("no se puede pasar a
     * EN_ATENCION sin CONFIRMAR primero") es consecuencia directa de
     * este mapa: EN_ATENCION solo es alcanzable desde EN_ESPERA, que a
     * su vez solo es alcanzable desde CONFIRMADA.
     */
    private static final Map<EstadoAtencion, Set<EstadoAtencion>> TRANSICIONES = new EnumMap<>(EstadoAtencion.class);

    static {
        TRANSICIONES.put(EstadoAtencion.SOLICITADA, EnumSet.of(EstadoAtencion.CONFIRMADA, EstadoAtencion.CANCELADA));
        TRANSICIONES.put(EstadoAtencion.CONFIRMADA, EnumSet.of(EstadoAtencion.EN_ESPERA, EstadoAtencion.CANCELADA));
        TRANSICIONES.put(EstadoAtencion.EN_ESPERA, EnumSet.of(EstadoAtencion.EN_ATENCION, EstadoAtencion.CANCELADA));
        TRANSICIONES.put(EstadoAtencion.EN_ATENCION, EnumSet.of(EstadoAtencion.CERRADA));
        TRANSICIONES.put(EstadoAtencion.CERRADA, EnumSet.noneOf(EstadoAtencion.class));
        TRANSICIONES.put(EstadoAtencion.CANCELADA, EnumSet.noneOf(EstadoAtencion.class));
    }

    private final AtencionRepository repository;
    private final PublicadorEventos publicador;

    public AtencionService(AtencionRepository repository, PublicadorEventos publicador) {
        this.repository = repository;
        this.publicador = publicador;
    }

    public Atencion crear(CrearAtencionRequest request) {
        Atencion atencion = new Atencion(
                request.pacienteId(),
                request.paciente(),
                request.prestacion(),
                request.boxId(),
                request.fecha()
        );
        Atencion guardada = repository.save(atencion);
        publicador.publicar(guardada);
        return guardada;
    }

    public Atencion obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AtencionNoEncontradaException(id));
    }

    /**
     * Para pacientes: si la atención no es suya responde 404 (no 403), así no
     * se revela que existe.
     */
    public Atencion obtenerDePaciente(Long id, String pacienteId) {
        Atencion atencion = obtener(id);
        if (!atencion.getPacienteId().equals(pacienteId)) {
            throw new AtencionNoEncontradaException(id);
        }
        return atencion;
    }

    public List<Atencion> listarDePaciente(String pacienteId, EstadoAtencion estado,
                                           LocalDateTime desde, LocalDateTime hasta) {
        return listar(estado, desde, hasta).stream()
                .filter(a -> a.getPacienteId().equals(pacienteId))
                .toList();
    }

    public List<Atencion> listar(EstadoAtencion estado, LocalDateTime desde, LocalDateTime hasta) {
        if (estado != null && desde != null && hasta != null) {
            return repository.findByEstadoAndFechaBetween(estado, desde, hasta);
        }
        if (estado != null) {
            return repository.findByEstado(estado);
        }
        if (desde != null && hasta != null) {
            return repository.findByFechaBetween(desde, hasta);
        }
        return repository.findAll();
    }

    public Atencion cambiarEstado(Long id, EstadoAtencion nuevoEstado) {
        Atencion atencion = obtener(id);
        Set<EstadoAtencion> permitidos = TRANSICIONES.get(atencion.getEstado());

        if (!permitidos.contains(nuevoEstado)) {
            throw new TransicionInvalidaException(
                    "No se puede pasar de " + atencion.getEstado() + " a " + nuevoEstado
            );
        }

        atencion.setEstado(nuevoEstado);
        Atencion actualizada = repository.save(atencion);
        publicador.publicar(actualizada);
        return actualizada;
    }
}
