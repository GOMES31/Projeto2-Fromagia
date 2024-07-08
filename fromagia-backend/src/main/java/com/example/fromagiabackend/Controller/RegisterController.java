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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.Binding;
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
    public String submitInitialForm(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,RedirectAttributes redirectAttributes, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "register/initialform";
        }

        AccountType accountType = user.getAccountType();

        if (accountType == null) {
            redirectAttributes.addFlashAttribute("error", "Please select an account type.");
            return "redirect:/register/initialform";
        }

        user.setAccountType(accountType);
        session.setAttribute("user", user);


        switch(accountType){
            case COMPANY:
                return "register/company";
            case CLIENT:
                return "register/client";
            case SUPPLIER:
                return "register/supplier";
            default:
                return "register/initialform";
        }
    }

// todo
    @GetMapping("/company")
    public String showCompanyForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "register/initialform";
        }

        Company company = new Company();

        user.setCompany(company);

        model.addAttribute("user", user);
        return "redirect:/register/company";
    }

    // todo

    @GetMapping("/client")
    public String showClientForm(Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "register/initialform";
        }

        Client client = new Client();

        user.setClient(client);
        model.addAttribute("user",user);
        return "redirect:/register/client";
    }

    // todo
    @GetMapping("/supplier")
    public String showSupplierForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "register/initialform";
        }

        Supplier supplier = new Supplier();

        user.setSupplier(supplier);

        model.addAttribute("user", user);
        return "redirect:/register/supplier";
    }
// todo
    @PostMapping("/company")
    public String submitCompanyForm(@ModelAttribute("user") User user, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "register/initialform";
        }

        if (bindingResult.hasErrors()) {
            return "register/initialform";
        }

        Company company = user.getCompany();
        Company newCompany = new Company(company.getName(),company.getEmail(),company.getContactNumber(),company.getNif());

        sessionUser.setCompany(newCompany);

        String redirect = verifyAndManageUser(redirectAttributes, session, sessionUser);

        companyService.save(newCompany);

        return redirect;
    }


    // TODO - FIXING THIS CODE
    @PostMapping("/client")
    public String submitClientForm(@ModelAttribute("user") @Valid User user,BindingResult bindingResult, RedirectAttributes redirectAttributes,HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "register/initialform";
        }

        if (bindingResult.hasErrors()) {
            return "register/client";
        }

        Client client = user.getClient();

        Client newClient = new Client(client.getName(), client.getEmail(), client.getContactNumber(), client.getNif());
        newClient.setUser(sessionUser);

        sessionUser.setClient(newClient);

        String redirect = verifyAndManageUser(redirectAttributes, session, sessionUser);

        clientService.save(newClient);

        return redirect;
    }

    // todo
    @PostMapping("/supplier")
    public String submitSupplierForm(@ModelAttribute("user") @Valid User user,BindingResult bindingResult, RedirectAttributes redirectAttributes,HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "register/initialform";
        }

        if (bindingResult.hasErrors()) {
            return "register/initialform";
        }

        Supplier supplier = user.getSupplier();
        Supplier newSupplier = new Supplier(supplier.getName(), supplier.getEmail(), supplier.getContactNumber(), supplier.getNif());

        sessionUser.setSupplier(newSupplier);

        String redirect = verifyAndManageUser(redirectAttributes, session, sessionUser);

        supplierService.save(newSupplier);


        return redirect;
    }


    private String verifyAndManageUser(RedirectAttributes redirectAttributes, HttpSession session, User sessionUser) {
        User tempUser = userService.findByUsername(sessionUser.getUsername());
        if (Objects.nonNull(tempUser)) {
            redirectAttributes.addFlashAttribute("error", "Username already registered. Please try again");
            return "redirect:/register/initialform";
        }

        userService.save(sessionUser);
        session.removeAttribute("user");
        redirectAttributes.addFlashAttribute("message", "Registration successful!");
        return "redirect:/auth/home";
    }
}
