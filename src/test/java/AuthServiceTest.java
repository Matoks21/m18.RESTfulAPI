import goit.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    public void testHasAuthority_True() {
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_ADMIN");
        Collection<GrantedAuthority> authorities = Collections.singleton(authority);
        when(user.getAuthorities()).thenReturn(authorities);

        boolean result = authService.hasAuthority("ROLE_ADMIN");

        assertTrue(result);
    }

    @Test
    public void testHasAuthority_False() {
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_USER");
        Collection<GrantedAuthority> authorities = Collections.singleton(authority);
        when(user.getAuthorities()).thenReturn(authorities);

        boolean result = authService.hasAuthority("ROLE_ADMIN");

        assertFalse(result);
    }

    @Test
    public void testHasAuthority_NoAuthorities() {
        when(user.getAuthorities()).thenReturn(Collections.emptyList());

        boolean result = authService.hasAuthority("ROLE_ADMIN");

        assertFalse(result);
    }
}
