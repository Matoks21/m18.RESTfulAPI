package goit.mvc;

import goit.auth.AuthService;
import goit.role.Role;
import goit.role.RoleDAO;
import goit.user.User;
import goit.user.UserDAO;
import goit.user.UserDTO;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
@RequestMapping("/user")
@Controller
public class UserController {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping
    public ModelAndView list() {
        if (!authService.hasAuthority("ADMIN")) {
            return new ModelAndView("front-page");
        }
        ModelAndView result = new ModelAndView("admin/user");
        String error = "";
        List<UserDTO> users = new ArrayList<>();
        try {
            for (User user : userDAO.findAll()) {
                users.add(UserDTO.fromUser(user));
            }
        } catch (Exception ex) {
            error = ex.getMessage();
        }
        result.addObject("users", users);
        result.addObject("result", error);
        return result;
    }

    @PostMapping("/update/{id}")
    public ModelAndView update(@PathVariable("id") UUID id,
                               @RequestParam("email") String email,
                               @RequestParam(value = "password", required = false) String password,
                               @RequestParam("firstName") String firstName,
                               @RequestParam("lastName") String lastName,
                               @RequestParam("roles") String roles) {
        if (!authService.hasAuthority("ADMIN")) {
            return new ModelAndView("front-page");
        }

        ModelAndView result = new ModelAndView("admin/user");
        String error = "";
        List<UserDTO> users;

        try {
            User user = userDAO.findById(id);
            user.setEmail(email);
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRoles(parser(roles));
            userDAO.save(user);
            error = "true";
        } catch (Exception ex) {
            error = ex.getMessage().equals("Could not commit JPA transaction; nested exception is " +
                    "javax.persistence.RollbackException: Error while committing the transaction")
                    ? "Invalid email address!"
                    : ex.getMessage();
        }

        users = userDAO.findAll().stream().map(UserDTO::fromUser).collect(Collectors.toList());
        System.out.println("users = " + users);
        result.addObject("users", users);
        result.addObject("result", error);
        return result;
    }

    private Set<Role> parser(String roles) {
        return Arrays.stream(roles.replace("[", "").replace("]", "").split(","))
                .map(String::strip)
                .map(roleDAO::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @PostMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable("id") UUID id) {
        if (!authService.hasAuthority("ADMIN")) {
            return new ModelAndView("front-page");
        }
        ModelAndView result = new ModelAndView("admin/user");
        String error;
        List<UserDTO> users = new ArrayList<>();
        try {
            userDAO.deleteById(id);
            users = userDAO.findAll().stream().map(UserDTO::fromUser).collect(Collectors.toList());
            error = "true";
        } catch (Exception ex) {
            error = ex.getMessage();
        }
        result.addObject("users", users);
        result.addObject("result", error);
        return result;
    }
}
