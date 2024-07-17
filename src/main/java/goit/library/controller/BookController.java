package goit.library.controller;


import goit.library.book.dto.WorkInfoDTO;
import goit.library.book.dto.WorkResponse;
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
public class BookController {

    private final LibraryService libraryService;
    private final UserRepository userRepository;
    private final SearchHistoryService searchHistoryService;

    @GetMapping("/works/search")
    public String searchAuthorsByWorkTitle(@RequestParam String title, Model model) {
        saveSearchHistory(title, "book");
        WorkResponse searchResponse = libraryService.searchAuthorsByWorkTitle(title);

        if (searchResponse.getDocs().isEmpty()) {
            return "error-page";
        }

        model.addAttribute("works", searchResponse.getDocs());
        return "work-list";
    }

    @GetMapping("/work/{title}")
    public String viewWorkDetails(@PathVariable String title, Model model) {
        WorkInfoDTO work = libraryService.getWorkByTitle(title);

        if (work == null) {
            model.addAttribute("error", "Work not found.");
            return "error-page";
        }

        model.addAttribute("work", work);
        return "work-details";
    }

    private void saveSearchHistory(String searchTerm, String searchType) {
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
