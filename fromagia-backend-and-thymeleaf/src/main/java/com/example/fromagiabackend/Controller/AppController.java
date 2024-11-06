package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController implements ErrorController {

    @GetMapping("/")
    public String redirectToInitialForm(){
        return "redirect:/register/initialform";
    }

    public static String getHomeRedirectUrl(User user){
        switch(user.getAccountType()){
            case COMPANY:
                return "redirect:/company/home";
            case CLIENT:
                return "redirect:/clients/home";
            case SUPPLIER:
                return "redirect:/suppliers/home";
            case EMPLOYEE:
                return "redirect:/employees/home";
            default:
                return "redirect:/auth/login";
        }
    }

    @GetMapping("/error")
    public String redirectToHomePage(HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        redirectAttributes.addFlashAttribute("error", "Erro inesperado aconteceu!");
        return getHomeRedirectUrl(currentUser);
    }

}
