package cl.duoc.vidasalud.appointments.controller;

import cl.duoc.vidasalud.appointments.dto.AtencionDtos.*;
import cl.duoc.vidasalud.appointments.entity.Atencion;
import cl.duoc.vidasalud.appointments.entity.EstadoAtencion;
import cl.duoc.vidasalud.appointments.service.AtencionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AtencionResponse> crear(@Valid @RequestBody CrearAtencionRequest request) {
        Atencion creada = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtencionResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(service.obtener(id)));
    }

    @GetMapping
    public ResponseEntity<List<AtencionResponse>> listar(
            @RequestParam(required = false) EstadoAtencion status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<AtencionResponse> resultado = service.listar(status, from, to).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(resultado);
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
                a.getBoxId(), a.getEstado(), a.getFecha()
        );
    }
}
