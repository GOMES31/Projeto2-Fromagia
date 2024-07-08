package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.User;
import com.example.fromagiabackend.Service.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService _userService){
        userService = _userService;
    }
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session){
        User currentUser = (User) session.getAttribute("currentUser");

        if (Objects.nonNull(currentUser)) {
            model.addAttribute("user",currentUser);
            return "home";
        }

        model.addAttribute("user",new User());

        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes, HttpSession session){
        User validUser = userService.findByUsername(user.getUsername());

        String returnUrl = "redirect:/login/login";
        String message;

        if (validUser == null) {
            message = "Invalid username. Please try again!";
        }
        else if (!validUser.getPassword().equals(user.getPassword())) {
            message = "Invalid password. Please try again!";
        }
        else {
            message = "Login successful!";
            session.setAttribute("currentUser", validUser);
            returnUrl = "home";
        }

        redirectAttributes.addFlashAttribute(validUser == null || !validUser.getPassword().equals(user.getPassword()) ? "error" : "message", message);
        return returnUrl;
    }

    @GetMapping("/home")
    public String showHomePage(Model model,HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("currentUser");

        if (Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error","You are not logged in. Please login to access the content!");
            return "redirect:/login/login";
        }

        model.addAttribute("user",currentUser);
        return "home";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }

        return "login/login";
    }

}
