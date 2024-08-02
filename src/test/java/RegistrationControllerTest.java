import goit.Application;

import goit.role.Role;
import goit.role.RoleDAO;
import goit.user.User;
import goit.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {Application.class , UserService.class, RoleDAO.class , PasswordEncoder.class })
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleDAO roleDAO;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        Role userRole = new Role();
        userRole.setName("USER");

        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(roleDAO.findByName(anyString())).thenReturn(userRole);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        mockMvc.perform(post("/register")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    public void testRegisterUser_ValidationErrors() throws Exception {
        User user = new User();
        user.setEmail("invalid email");  // Неправильний формат email
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");

        mockMvc.perform(post("/register")
                        .flashAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));
    }



    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        User user = new User();
        user.setEmail("user@gmail.com");  // Існуючий email
        user.setPassword("jdbcDefault");
        user.setFirstName("User");
        user.setLastName("User");

        when(userService.existsByEmail(anyString())).thenReturn(true);

        mockMvc.perform(post("/register")
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .param("firstName", user.getFirstName())
                        .param("lastName", user.getLastName()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("user", "email"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "error.user"));

        verify(userService, never()).registerUser(any(User.class));
    }
}