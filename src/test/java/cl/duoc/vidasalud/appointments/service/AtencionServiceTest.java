package cl.duoc.vidasalud.appointments.service;

import cl.duoc.vidasalud.appointments.dto.AtencionDtos.CrearAtencionRequest;
import cl.duoc.vidasalud.appointments.entity.Atencion;
import cl.duoc.vidasalud.appointments.entity.EstadoAtencion;
import cl.duoc.vidasalud.appointments.exception.TransicionInvalidaException;
import cl.duoc.vidasalud.appointments.repository.AtencionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtencionServiceTest {

    @Mock
    private AtencionRepository repository;

    private AtencionService service;

    @BeforeEach
    void setUp() {
        service = new AtencionService(repository);
    }

    @Test
    void creaAtencionEnEstadoSolicitada() {
        CrearAtencionRequest request = new CrearAtencionRequest(
                "pac-1", "Juan Pérez", "Consulta general", 3L, LocalDateTime.now().plusDays(1));

        when(repository.save(any(Atencion.class))).thenAnswer(inv -> inv.getArgument(0));

        Atencion creada = service.crear(request);

        assertThat(creada.getEstado()).isEqualTo(EstadoAtencion.SOLICITADA);
    }

    @Test
    void noPermiteSaltarDeSolicitadaAEnAtencion() {
        Atencion atencion = new Atencion("pac-1", "Juan Pérez", "Consulta", 3L, LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(atencion));

        assertThatThrownBy(() -> service.cambiarEstado(1L, EstadoAtencion.EN_ATENCION))
                .isInstanceOf(TransicionInvalidaException.class)
                .hasMessageContaining("SOLICITADA");
    }

    @Test
    void permiteFlujoCompletoDeConfirmacionHastaEnEspera() {
        Atencion atencion = new Atencion("pac-1", "Juan Pérez", "Consulta", 3L, LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(atencion));
        when(repository.save(any(Atencion.class))).thenAnswer(inv -> inv.getArgument(0));

        service.cambiarEstado(1L, EstadoAtencion.CONFIRMADA);
        Atencion resultado = service.cambiarEstado(1L, EstadoAtencion.EN_ESPERA);

        assertThat(resultado.getEstado()).isEqualTo(EstadoAtencion.EN_ESPERA);
    }
}
