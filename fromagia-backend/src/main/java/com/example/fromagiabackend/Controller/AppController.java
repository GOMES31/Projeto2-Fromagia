package com.example.fromagiabackend.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("/")
    public String redirectToInitialForm(){
        return "redirect:/register/initialform";
    }

}
