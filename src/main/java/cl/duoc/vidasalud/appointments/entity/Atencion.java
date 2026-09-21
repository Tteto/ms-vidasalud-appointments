package cl.duoc.vidasalud.appointments.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ATENCIONES")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pacienteId;

    @Column(nullable = false)
    private String paciente;

    @Column(nullable = false)
    private String prestacion;

    @Column(nullable = false)
    private Long boxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAtencion estado = EstadoAtencion.SOLICITADA;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadaEn = LocalDateTime.now();

    private LocalDateTime actualizadaEn = LocalDateTime.now();

    protected Atencion() {
        // requerido por JPA
    }

    public Atencion(String pacienteId, String paciente, String prestacion, Long boxId, LocalDateTime fecha) {
        this.pacienteId = pacienteId;
        this.paciente = paciente;
        this.prestacion = prestacion;
        this.boxId = boxId;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getPaciente() { return paciente; }
    public String getPrestacion() { return prestacion; }
    public Long getBoxId() { return boxId; }
    public EstadoAtencion getEstado() { return estado; }
    public LocalDateTime getFecha() { return fecha; }
    public LocalDateTime getCreadaEn() { return creadaEn; }
    public LocalDateTime getActualizadaEn() { return actualizadaEn; }

    public void setEstado(EstadoAtencion estado) {
        this.estado = estado;
        this.actualizadaEn = LocalDateTime.now();
    }
}
