package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("/")
    public String redirectToInitialForm(){
        return "redirect:/register/initialform";
    }

    public static String getHomeRedirectUrl(User user){
        switch(user.getAccountType()){
            case COMPANY:
                return "redirect:/company/home";
            case CLIENT:
                return "redirect:/client/home";
            case SUPPLIER:
                return "redirect:/supplier/home";
            case EMPLOYEE:
                return "redirect:/employee/home";
            default:
                return "redirect:/auth/login";
        }
    }

}
