package cl.duoc.vidasalud.appointments.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public record UsuarioActual(String id, String nombre, boolean personal) {

    public static UsuarioActual desde(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();

        String id = jwt.getClaimAsString("oid");
        if (id == null) {
            id = jwt.getSubject();
        }

        String nombre = jwt.getClaimAsString("name");
        if (nombre == null) {
            nombre = jwt.getClaimAsString("preferred_username");
        }

        boolean personal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_RECEPCIONISTA"));

        return new UsuarioActual(id, nombre, personal);
    }
}