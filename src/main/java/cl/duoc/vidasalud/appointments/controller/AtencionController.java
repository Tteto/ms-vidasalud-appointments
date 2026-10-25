package cl.duoc.vidasalud.appointments.controller;

import cl.duoc.vidasalud.appointments.dto.AtencionDtos.*;
import cl.duoc.vidasalud.appointments.entity.Atencion;
import cl.duoc.vidasalud.appointments.entity.EstadoAtencion;
import cl.duoc.vidasalud.appointments.security.UsuarioActual;
import cl.duoc.vidasalud.appointments.service.AtencionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AtencionController {

    private final AtencionService service;

    public AtencionController(AtencionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AtencionResponse> crear(@Valid @RequestBody CrearAtencionRequest request,
            Authentication auth) {
        UsuarioActual usuario = UsuarioActual.desde(auth);

        // Un paciente solo puede agendar a su nombre: se usan los datos del token,
        // no los del body.
        CrearAtencionRequest datos = usuario.personal()
                ? request
                : new CrearAtencionRequest(usuario.id(), usuario.nombre(),
                        request.prestacion(), request.boxId(), request.fecha());

        Atencion creada = service.crear(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtencionResponse> obtener(@PathVariable Long id, Authentication auth) {
        UsuarioActual usuario = UsuarioActual.desde(auth);
        Atencion atencion = usuario.personal()
                ? service.obtener(id)
                : service.obtenerDePaciente(id, usuario.id());
        return ResponseEntity.ok(toResponse(atencion));
    }

    @GetMapping
    public ResponseEntity<List<AtencionResponse>> listar(
            @RequestParam(required = false) EstadoAtencion status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Authentication auth) {
        UsuarioActual usuario = UsuarioActual.desde(auth);
        List<Atencion> atenciones = usuario.personal()
                ? service.listar(status, from, to)
                : service.listarDePaciente(usuario.id(), status, from, to);

        return ResponseEntity.ok(atenciones.stream().map(this::toResponse).toList());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AtencionResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        Atencion actualizada = service.cambiarEstado(id, request.status());
        return ResponseEntity.ok(toResponse(actualizada));
    }

    private AtencionResponse toResponse(Atencion a) {
        return new AtencionResponse(
                a.getId(), a.getPacienteId(), a.getPaciente(), a.getPrestacion(),
                a.getBoxId(), a.getEstado(), a.getFecha());
    }
}