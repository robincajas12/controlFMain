package com.controlf.auth;

import com.controlf.db.repository.UsuarioRepository;
import com.controlf.db.schema.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carga los principales de Spring Security desde la tabla {@code usuarios},
 * buscando por email. Lo usa el proveedor de autenticación de
 * {@link SecurityConfig} y, de forma indirecta, {@link JwtAuthenticationFilter}
 * al reconstruir el principal a partir de un JWT ya validado.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Busca un usuario por email y lo adapta a un {@link UserDetails} de
     * Spring Security.
     *
     * @param username el email ingresado en el login
     * @return el {@link AuthenticatedUser} correspondiente
     * @throws UsernameNotFoundException si ningún usuario tiene ese email
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getNombre(), user.getRol().name(), user.getPasswordHash(), user.isActivo());
    }
}
