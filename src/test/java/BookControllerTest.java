import goit.library.book.dto.WorkInfoDTO;
import goit.library.book.dto.WorkResponse;
import goit.library.controller.BookController;
import goit.library.service.LibraryService;
import goit.search.SearchHistory;
import goit.search.SearchHistoryService;
import goit.user.User;
import goit.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LibraryService libraryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchHistoryService searchHistoryService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Set up SecurityContext with a mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@mail.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Create a mock user and set it in the userRepository
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("user@mail.com");
        user.setFirstName("John");
        when(userRepository.findByEmail("user@mail.com")).thenReturn(user);

        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void testSearchAuthorsByWorkTitle() throws Exception {
        WorkResponse response = new WorkResponse();
        response.setDocs(Collections.singletonList(new WorkInfoDTO())); // Заповнюємо mock даними

        when(libraryService.searchAuthorsByWorkTitle("testTitle")).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/library/works/search").param("title", "testTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("work-list"))
                .andExpect(model().attributeExists("works"));

        verify(searchHistoryService, times(1)).saveSearchHistory(any(SearchHistory.class));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void testViewWorkDetails() throws Exception {
        WorkInfoDTO work = new WorkInfoDTO();
        when(libraryService.getWorkByTitle("testTitle")).thenReturn(work);

        mockMvc.perform(MockMvcRequestBuilders.get("/library/work/testTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("work-details"))
                .andExpect(model().attributeExists("work"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    public void testViewWorkDetailsNotFound() throws Exception {
        when(libraryService.getWorkByTitle("testTitle")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/library/work/testTitle"))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attributeExists("error"));
    }
}
