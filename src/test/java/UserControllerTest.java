
import goit.auth.AuthService;
import goit.mvc.UserController;
import goit.role.Role;
import goit.role.RoleDAO;
import goit.user.User;
import goit.user.UserDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class UserControllerTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private RoleDAO roleDAO;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testList_AdminAccess() throws Exception {
        when(authService.hasAuthority("ADMIN")).thenReturn(true);
        List<User> users = Arrays.asList(new User(UUID.randomUUID(), "test@example.com", "password", "John", "Doe", new HashSet<>()));
        when(userDAO.findAll()).thenReturn(users);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("result"));

        verify(userDAO, times(1)).findAll();
    }

    @Test
    public void testList_NonAdminAccess() throws Exception {
        when(authService.hasAuthority("ADMIN")).thenReturn(false);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("front-page"));

        verify(userDAO, never()).findAll();
    }

    @Test
    public void testUpdate_AdminAccess() throws Exception {
        when(authService.hasAuthority("ADMIN")).thenReturn(true);
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "test@example.com", "password", "John", "Doe", new HashSet<>());
        when(userDAO.findById(userId)).thenReturn(user);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");

        mockMvc.perform(post("/user/update/" + userId)
                        .param("email", "new@example.com")
                        .param("password", "newPassword")
                        .param("firstName", "New")
                        .param("lastName", "Name")
                        .param("roles", "[ROLE_USER]"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("result", "true"));

        verify(userDAO, times(1)).save(any(User.class));
    }

    @Test
    public void testDelete_AdminAccess() throws Exception {
        when(authService.hasAuthority("ADMIN")).thenReturn(true);
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/user/delete/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("result", "true"));

        verify(userDAO, times(1)).deleteById(userId);
    }
}
