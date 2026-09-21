package cl.duoc.vidasalud.appointments.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Este microservicio vive detrás del BFF y del API Gateway, que ya
 * validan el JWT. Pero se vuelve a validar acá (defensa en
 * profundidad): si alguna vez el API Gateway apunta directo a este
 * servicio, no debe quedar abierto solo porque el BFF esté bien
 * configurado.
 */
@Component
public class RolesConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        List<GrantedAuthority> authorities = (roles == null ? List.<String>of() : roles).stream()
                .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                .collect(Collectors.toList());

        JwtAuthenticationConverter delegate = new JwtAuthenticationConverter();
        delegate.setJwtGrantedAuthoritiesConverter(source -> authorities);
        return delegate.convert(jwt);
    }
}
