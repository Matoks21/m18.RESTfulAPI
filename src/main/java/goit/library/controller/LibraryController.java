package goit.library.controller;


import goit.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@RequiredArgsConstructor
@Controller
@RequestMapping("/library")
public class LibraryController {

    private final AuthService authService;

    @GetMapping("/menu")
    public String showMainPage(Model model) {
        boolean admin = isAdmin();
        model.addAttribute("isAdmin", admin);
        return "front-page";
    }

    private boolean isAdmin() {
        return authService.hasAuthority("ADMIN");
    }




}
