package goit.library.controller;


import goit.library.dto.AuthorInfoDTO;
import goit.library.dto.AuthorSearchResponse;
import goit.library.service.LibraryService;
import goit.search.SearchHistory;
import goit.search.SearchHistoryService;
import goit.user.User;
import goit.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@AllArgsConstructor
@Controller
@RequestMapping("/library")
public class AuthorController {

    private final LibraryService libraryService;
    private final UserRepository userRepository;
    private final SearchHistoryService searchHistoryService;



    @GetMapping("/authors/search")
    public String searchAuthorsByName(@RequestParam String name, Model model) {
        saveSearchHistory(name, "author");
        AuthorSearchResponse searchResponse = libraryService.searchAuthorsByName(name);

        if (searchResponse == null || searchResponse.getDocs().isEmpty()) {
            model.addAttribute("error", "Author not found or no results found.");
            return "error-page";
        }

        model.addAttribute("authors", searchResponse.getDocs());
        return "author-list";
    }

    @GetMapping("/author/{key}")
    public String viewAuthorDetails(@PathVariable String key, Model model) {
        AuthorInfoDTO author = libraryService.getAuthorByKey(key);

        if (author == null) {
            model.addAttribute("error", "Author not found.");
            return "error-page";
        }

        model.addAttribute("author", author);
        return "author-details";
    }

    public void saveSearchHistory(String searchTerm, String searchType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUserId(user.getId());
        searchHistory.setUsername(user.getEmail());
        searchHistory.setFirstName(user.getFirstName());
        searchHistory.setSearchTerm(searchTerm);
        searchHistory.setSearchType(searchType);
        searchHistory.setTimestamp(LocalDateTime.now());
        searchHistoryService.saveSearchHistory(searchHistory);


    }
}

