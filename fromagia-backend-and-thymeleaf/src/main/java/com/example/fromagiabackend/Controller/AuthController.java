package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.User;
import com.example.fromagiabackend.Service.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

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
        User currentUser = (User) session.getAttribute("user");

        if (Objects.nonNull(currentUser)) {
            model.addAttribute("user",currentUser);
            return getHomeRedirectUrl(currentUser);
        }

        model.addAttribute("user",new User());

        return "auth/login";
    }

    @PostMapping("/login")
    public String login( @ModelAttribute("user") @Valid User user, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session){

        if(bindingResult.hasErrors()){
            return "auth/login";
        }

        User dbUser = userService.findByUsername(user.getUsername());

        if(Objects.isNull(dbUser)) {
            bindingResult.rejectValue("username", "error.user", "Utilizador não encontrado!");
            return "auth/login";
        }

        String dbPassword = dbUser.getPassword();
        String inputPassword = user.getPassword();


        if(!Objects.equals(dbPassword,inputPassword)){
            bindingResult.rejectValue("password", "error.password", "Password incorreta!");
            return "auth/login";
        }

        session.setAttribute("user",dbUser);
        redirectAttributes.addFlashAttribute("message","Login efetuado com sucesso!");

        return getHomeRedirectUrl(dbUser);

    }

    @GetMapping("/logout")
    public String logout(HttpSession session){

        session.invalidate();

        return "redirect:/auth/login";
    }


}
