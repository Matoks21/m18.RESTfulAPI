import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import goit.library.controller.AuthorController;
import goit.library.dto.AuthorInfoDTO;
import goit.library.dto.AuthorSearchResponse;
import goit.library.service.LibraryService;
import goit.search.SearchHistory;
import goit.search.SearchHistoryService;
import goit.user.User;
import goit.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;


public class AuthorControllerTest {

    @Mock
    private LibraryService libraryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchHistoryService searchHistoryService;

    @InjectMocks
    private AuthorController authorController;
    @BeforeEach

    void setUp() {
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
        user.setPassword("password");
        when(userRepository.findByEmail("user@mail.com")).thenReturn(user);
    }


    @Test
    @WithMockUser(username = "user@mail.com")
    void testSearchAuthorsByName() {
        Model model = mock(Model.class);
        AuthorSearchResponse response = new AuthorSearchResponse();
        response.setDocs(Collections.singletonList(new AuthorInfoDTO()));

        when(libraryService.searchAuthorsByName("Rowling")).thenReturn(response);

        String result = authorController.searchAuthorsByName("Rowling", model);

        assertEquals("author-list", result);
        verify(model, times(1)).addAttribute("authors", response.getDocs());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void testSearchAuthorsByNameNotFound() {
        Model model = mock(Model.class);
        when(libraryService.searchAuthorsByName("testAuthor")).thenReturn(null);

        String result = authorController.searchAuthorsByName("testAuthor", model);

        assertEquals("error-page", result);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }


    @Test
    @WithMockUser(username = "user@mail.com")
    void testViewAuthorDetails() {
        Model model = mock(Model.class);
        AuthorInfoDTO author = new AuthorInfoDTO();
        when(libraryService.getAuthorByKey("authorKey")).thenReturn(author);

        String result = authorController.viewAuthorDetails("authorKey", model);

        assertEquals("author-details", result);
        verify(model, times(1)).addAttribute("author", author);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void testViewAuthorDetailsNotFound() {
        Model model = mock(Model.class);
        when(libraryService.getAuthorByKey("authorKey")).thenReturn(null);

        String result = authorController.viewAuthorDetails("authorKey", model);

        assertEquals("error-page", result);
        verify(model, times(1)).addAttribute(eq("error"), anyString());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void testSaveSearchHistory() {
        User user = new User();
        user.setId(user.getId());
        user.setEmail("user@mail.com");
        user.setFirstName("John");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userRepository.findByEmail("user@mail.com")).thenReturn(user);

        authorController.saveSearchHistory("testSearchTerm", "author");

        verify(searchHistoryService, times(1)).saveSearchHistory(any(SearchHistory.class));
    }
}
