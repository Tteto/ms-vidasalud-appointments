package cl.duoc.vidasalud.appointments.exception;

public class AtencionNoEncontradaException extends RuntimeException {
    public AtencionNoEncontradaException(Long id) {
        super("No existe la atención con id " + id);
    }
}
