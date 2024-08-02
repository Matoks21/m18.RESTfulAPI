import goit.Application;
import goit.auth.CustomUserDetailsService;
import goit.auth.DefaultSecurityConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, DefaultSecurityConfig.class, CustomUserDetailsService.class})
@AutoConfigureMockMvc
public class DefaultSecurityConfigTest {

    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webAppContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        this.mockMvc.perform(formLogin("/login").user("user@gmail.com").password("jdbcDefault"))
                .andExpect(authenticated());
    }

    @Test
    public void testLoginFailure() throws Exception {
        this.mockMvc.perform(formLogin("/login").user("user@gmail.com").password("wrongpassword"))
                .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser
    public void testAccessProtectedUrl() throws Exception {
        this.mockMvc.perform(get("/library/menu"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAccessProtectedUrlUnauthenticated() throws Exception {
        this.mockMvc.perform(get("/library/menu"))
                .andExpect(status().is3xxRedirection());
    }
}