package com.controlf.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Autentica cada petición a partir de un JWT tipo {@code Bearer} en el
 * header {@code Authorization}, ejecutándose una sola vez por petición
 * antes que los filtros propios de Spring Security. Las peticiones sin un
 * token válido simplemente continúan sin autenticar, ya que los endpoints
 * públicos están permitidos en {@link SecurityConfig} y los protegidos
 * serán rechazados más adelante en la cadena.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Valida el token bearer, si está presente, y coloca el principal
     * resuelto en el {@link SecurityContextHolder} antes de continuar la
     * cadena de filtros. Cualquier fallo al parsear o validar el token se
     * trata como "no autenticado" en lugar de un error.
     *
     * @param request petición HTTP entrante
     * @param response respuesta HTTP saliente
     * @param filterChain resto de la cadena de filtros a invocar
     * @throws ServletException si la cadena de filtros posterior falla
     * @throws IOException si la cadena de filtros posterior falla
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var claims = jwtService.parseToken(token);
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);
                if (email != null && role != null) {
                    var principal = (AuthenticatedUser) customUserDetailsService.loadUserByUsername(email);
                    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
