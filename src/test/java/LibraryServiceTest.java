
import goit.library.book.dto.WorkInfoDTO;
import goit.library.book.dto.WorkResponse;
import goit.library.dto.AuthorInfoDTO;
import goit.library.dto.AuthorSearchResponse;
import goit.library.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LibraryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LibraryService libraryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchAuthorsByName() {
        AuthorSearchResponse mockResponse = new AuthorSearchResponse();
        ResponseEntity<AuthorSearchResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(AuthorSearchResponse.class)))
                .thenReturn(responseEntity);

        AuthorSearchResponse result = libraryService.searchAuthorsByName("testAuthor");

        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(AuthorSearchResponse.class));
    }

    @Test
    public void testSearchAuthorsByWorkTitle() {
        WorkResponse mockResponse = new WorkResponse();
        ResponseEntity<WorkResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(WorkResponse.class)))
                .thenReturn(responseEntity);

        WorkResponse result = libraryService.searchAuthorsByWorkTitle("testTitle");

        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(WorkResponse.class));
    }

    @Test
    public void testGetAuthorByKey() {
        String mockHtml = "<html><h1 itemprop='name'>Test Author</h1><div itemprop='description'><p>Test Description</p></div><div class='SRPCover bookCover'><img itemprop='image' src='/test.jpg'></div></html>";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockHtml, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(String.class)))
                .thenReturn(responseEntity);

        AuthorInfoDTO result = libraryService.getAuthorByKey("testKey");

        assertNotNull(result);
        assertEquals("Test Author", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals("https:/test.jpg", result.getImageUrl());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(String.class));
    }

    @Test
    public void testGetWorkByTitle() {
        WorkInfoDTO workInfoDTO = new WorkInfoDTO();
        workInfoDTO.setKey("/works/OL12345W");
        WorkResponse workResponse = new WorkResponse();
        workResponse.setDocs(Collections.singletonList(workInfoDTO));
        ResponseEntity<WorkResponse> responseEntity = new ResponseEntity<>(workResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(WorkResponse.class)))
                .thenReturn(responseEntity);

        String mockHtml = "<html><div class='read-more__content'><p>Full description here.</p></div></html>";
        ResponseEntity<String> htmlResponseEntity = new ResponseEntity<>(mockHtml, HttpStatus.OK);
        when(restTemplate.exchange(contains("/works/OL12345W"), eq(HttpMethod.GET), eq(null), eq(String.class)))
                .thenReturn(htmlResponseEntity);

        WorkInfoDTO result = libraryService.getWorkByTitle("testTitle");

        assertNotNull(result);
        assertEquals("Full description here.", result.getDescription());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(WorkResponse.class));
        verify(restTemplate, times(1)).exchange(contains("/works/OL12345W"), eq(HttpMethod.GET), eq(null), eq(String.class));
    }
}
