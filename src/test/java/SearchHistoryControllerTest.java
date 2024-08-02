import goit.Application;
import goit.auth.AuthService;
import goit.auth.CustomUserDetailsService;
import goit.library.controller.SearchHistoryController;
import goit.search.SearchHistoryRepository;
import goit.search.SearchHistoryService;
import goit.user.User;
import goit.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {Application.class, CustomUserDetailsService.class, AuthService.class})
public class SearchHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchHistoryRepository searchHistoryRepository;

    @MockBean
    private SearchHistoryService searchHistoryService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @InjectMocks
    private SearchHistoryController searchHistoryController;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void testViewSearchHistory() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(searchHistoryRepository.findByUsername("user@example.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/library/search-history"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-history"))
                .andExpect(model().attribute("searchHistory", Collections.emptyList()));
    }

    @Test
    @WithMockUser(username = "admin@example.com", authorities = "ADMIN")
    public void testGetSearchHistoryForUser() throws Exception {
        UUID userId = UUID.randomUUID();
        when(searchHistoryService.getSearchHistoryByUserId(userId)).thenReturn(Collections.emptyList());
        when(authService.hasAuthority("ADMIN")).thenReturn(true);

        mockMvc.perform(post("/search-history/" + userId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("search-history"))
                .andExpect(model().attribute("searchHistory", Collections.emptyList()))
                .andExpect(model().attribute("isAdmin", true));
    }
}