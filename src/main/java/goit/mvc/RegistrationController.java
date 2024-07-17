package goit.mvc;


import goit.role.Role;
import goit.role.RoleDAO;
import goit.user.User;
import goit.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Collections;





@Controller
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final UserService userService;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserService userService, RoleDAO roleDAO, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult result, Model model) {
        logger.info("Attempting to register user: {}", user.getEmail());

        if (result.hasErrors()) {
            logger.info("Validation errors found: {}", result.getAllErrors());
            return "registration";
        }

        if (userService.existsByEmail(user.getEmail())) {
            logger.info("Email {} already in use", user.getEmail());
            result.rejectValue("email", "error.user", "Email already in use. Please use a different email.");
            return "registration";
        }
        Role defaultRole = roleDAO.findByName("USER");
        user.setRoles(Collections.singleton(defaultRole));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.registerUser(user);

        logger.info("User {} registered successfully", user.getEmail());
        return "redirect:/login";
    }
}
