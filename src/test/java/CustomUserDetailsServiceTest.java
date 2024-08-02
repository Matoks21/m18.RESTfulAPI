

import goit.auth.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CustomUserDetailsServiceTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        CustomUserDetailsService.UserDate mockUserDate = CustomUserDetailsService.UserDate.builder()
                .password("password123")
                .authority("ROLE_USER,ROLE_ADMIN")
                .build();
        when(jdbcTemplate.queryForObject(anyString(), any(Map.class), any(CustomUserDetailsService.UserDataMapper.class)))
                .thenReturn(mockUserDate);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user@mail.com");

        assertNotNull(userDetails);
        assertEquals("password123", userDetails.getPassword());
        assertEquals("user@mail.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(Map.class), any(CustomUserDetailsService.UserDataMapper.class));
    }

    @Test
    public void testLoadUserByUsername_UserDoesNotExist() {
        when(jdbcTemplate.queryForObject(anyString(), any(Map.class), any(CustomUserDetailsService.UserDataMapper.class)))
                .thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("user@mail.com"));
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(Map.class), any(CustomUserDetailsService.UserDataMapper.class));
    }

    @Test
    public void testGetByIdOrNull_UserExists() {
       CustomUserDetailsService.UserDate mockUserDate = CustomUserDetailsService.UserDate.builder()
                .password("password123")
                .authority("ROLE_USER,ROLE_ADMIN")
                .build();
        when(jdbcTemplate.queryForObject(anyString(), any(Map.class), any(CustomUserDetailsService.UserDataMapper.class)))
                .thenReturn(mockUserDate);

        CustomUserDetailsService.UserDate result = customUserDetailsService.getByIdOrNull("user@mail.com");

        assertNotNull(result);
        assertEquals("password123", result.getPassword());
        assertEquals("ROLE_USER,ROLE_ADMIN", result.getAuthority());
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(Map.class), any(CustomUserDetailsService.UserDataMapper.class));
    }

    @Test
    public void testGetByIdOrNull_UserDoesNotExist() {
        when(jdbcTemplate.queryForObject(anyString(), any(Map.class), any(CustomUserDetailsService.UserDataMapper.class)))
                .thenReturn(null);

        CustomUserDetailsService.UserDate result = customUserDetailsService.getByIdOrNull("user@mail.com");

        assertNull(result);
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(Map.class), any(CustomUserDetailsService.UserDataMapper.class));
    }
}
