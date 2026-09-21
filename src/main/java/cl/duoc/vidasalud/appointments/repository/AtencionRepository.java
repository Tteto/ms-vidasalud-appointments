package cl.duoc.vidasalud.appointments.repository;

import cl.duoc.vidasalud.appointments.entity.Atencion;
import cl.duoc.vidasalud.appointments.entity.EstadoAtencion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {

    List<Atencion> findByEstado(EstadoAtencion estado);

    List<Atencion> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Atencion> findByEstadoAndFechaBetween(EstadoAtencion estado, LocalDateTime desde, LocalDateTime hasta);
}
