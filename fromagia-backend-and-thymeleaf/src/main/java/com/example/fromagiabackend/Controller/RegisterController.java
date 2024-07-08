package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.Client;
import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Supplier;
import com.example.fromagiabackend.Entity.User;
import com.example.fromagiabackend.Service.Client.ClientService;
import com.example.fromagiabackend.Service.Company.CompanyService;
import com.example.fromagiabackend.Service.Supplier.SupplierService;
import com.example.fromagiabackend.Service.User.UserService;
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


@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final CompanyService companyService;
    private final ClientService clientService;
    private final SupplierService supplierService;

    @Autowired
    public RegisterController(UserService _userService,CompanyService _companyService,ClientService _clientService,SupplierService _supplierService){
        userService = _userService;
        companyService = _companyService;
        clientService = _clientService;
        supplierService = _supplierService;
    }


    @GetMapping("/initialform")
    public String showInitialForm(Model model){
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register/initialform";
    }

    @PostMapping("/initialform")
    public String submitInitialForm(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "register/initialform";
        }

        User dbUser = userService.findByUsername(user.getUsername());
        if (Objects.nonNull(dbUser)) {
            bindingResult.rejectValue("username", "error.user", "Utilizador já registado!");
            return "register/initialform";
        }


        AccountType accountType = user.getAccountType();

        user.setAccountType(accountType);
        session.setAttribute("user", user);


        switch(accountType){
            case COMPANY:
                return "redirect:/register/company";
            case CLIENT:
                return "redirect:/register/client";
            case SUPPLIER:
                return "redirect:/register/supplier";
            default:
                return "redirect:/register/initialform";
        }
    }


    @GetMapping("/company")
    public String showCompanyForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/register/initialform";
        }

        Company company = new Company();

        model.addAttribute("company",company);
        return "register/company";
    }


    @GetMapping("/client")
    public String showClientForm(Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/register/initialform";
        }

        Client client = new Client();

        model.addAttribute("client",client);
        return "register/client";
    }

    @GetMapping("/supplier")
    public String showSupplierForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/register/initialform";
        }

        Supplier supplier = new Supplier();

        model.addAttribute("supplier",supplier);
        return "register/supplier";
    }


    @PostMapping("/company")
    public String submitCompanyForm(@ModelAttribute("company") @Valid Company company, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "register/company";
        }

        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/register/initialform";
        }

        Company newCompany = new Company(company.getName(), company.getEmail(), company.getContactNumber(), company.getNif());
        newCompany.setUser(sessionUser);

        sessionUser.setCompany(newCompany);

        userService.save(sessionUser);
        redirectAttributes.addFlashAttribute("message", "Registo feito com sucesso!");


        companyService.save(newCompany);

        session.setAttribute("user",sessionUser);

        return "redirect:/auth/home";
    }



    @PostMapping("/client")
    public String submitClientForm(@ModelAttribute("client") @Valid Client client,BindingResult bindingResult, RedirectAttributes redirectAttributes,HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "register/client";
        }


        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/register/initialform";
        }

        Client newClient = new Client(client.getName(), client.getEmail(), client.getContactNumber(), client.getNif());
        newClient.setUser(sessionUser);

        sessionUser.setClient(newClient);

        userService.save(sessionUser);
        redirectAttributes.addFlashAttribute("message", "Registo feito com sucesso!");


        clientService.save(newClient);

        session.setAttribute("user",sessionUser);

        return "redirect:/auth/home";
    }

    @PostMapping("/supplier")
    public String submitSupplierForm(@ModelAttribute("supplier") @Valid Supplier supplier, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "register/supplier";
        }

        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/register/initialform";
        }

        Supplier newSupplier = new Supplier(supplier.getName(), supplier.getEmail(), supplier.getContactNumber(), supplier.getNif());
        newSupplier.setUser(sessionUser);

        sessionUser.setSupplier(newSupplier);

        userService.save(sessionUser);
        redirectAttributes.addFlashAttribute("message", "Registo feito com sucesso!");


        supplierService.save(newSupplier);

        session.setAttribute("user",sessionUser);

        return "redirect:/auth/home";
    }
}
