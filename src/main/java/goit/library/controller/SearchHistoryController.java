package goit.library.controller;


import goit.auth.AuthService;
import goit.search.SearchDTO;
import goit.search.SearchHistory;
import goit.search.SearchHistoryRepository;
import goit.search.SearchHistoryService;
import goit.user.User;
import goit.user.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
@Controller

public class SearchHistoryController {

    private final SearchHistoryRepository searchHistoryRepository;
    private final SearchHistoryService searchHistoryService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/library/search-history")
    public String viewSearchHistory(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        List<SearchHistory> searchHistory = searchHistoryRepository.findByUsername(user.getEmail());
        model.addAttribute("searchHistory", searchHistory);
        return "search-history";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/search-history/{userId}")
    public String getSearchHistoryForUser(@PathVariable UUID userId, Model model) {
        List<SearchHistory> searchHistory = searchHistoryService.getSearchHistoryByUserId(userId);
        List<SearchDTO> searchHistoryDTOs = searchHistory.stream()
                .map(history -> {
                    SearchDTO dto = new SearchDTO();
                    dto.setUserId(history.getUserId());
                    dto.setUsername(history.getUsername());
                    dto.setFirstName(history.getFirstName());
                    dto.setSearchType(history.getSearchType());
                    dto.setSearchTerm(history.getSearchTerm());
                    dto.setTimestamp(history.getTimestamp());
                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("searchHistory", searchHistoryDTOs);

        model.addAttribute("isAdmin", isAdmin());
        System.out.println("isAdmin = " + isAdmin());

        return "search-history";
    }

    private boolean isAdmin() {
        return authService.hasAuthority("ADMIN");
    }
}

