package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.*;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Service.Company.CompanyService;
import com.example.fromagiabackend.Service.Stock.StockService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;
    private final StockService stockService;

    @Autowired
    public CompanyController(CompanyService _companyService,StockService _stockService){

        companyService = _companyService;
        stockService = _stockService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        if(Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error","Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if(currentUser.getAccountType() != AccountType.COMPANY){
            redirectAttributes.addFlashAttribute("error","Não tens permissões para aceder a esta página!");
            return "redirect:/auth/logout";
        }

        model.addAttribute("user",currentUser);
        return "company/home";
    }

    @GetMapping("/orders")
    public String showOrdersPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        if(Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error","Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if(!Objects.equals(AccountType.COMPANY,currentUser.getAccountType())){
            redirectAttributes.addFlashAttribute("error","Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }


        Company company = currentUser.getCompany();

        if(company == null){
            redirectAttributes.addFlashAttribute("error","Este utilizador não tem nenhuma empresa associada!");
            return getHomeRedirectUrl(currentUser);
        }

        List<Order> orders = companyService.getCompanyOrders(company.getId());

        model.addAttribute("company",company);
        model.addAttribute("orders",orders);
        return "company/orders";
    }

    @GetMapping("/stock")
    public String showCompanyStockPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        if(Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error","Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if(!Objects.equals(AccountType.COMPANY,currentUser.getAccountType())){
            redirectAttributes.addFlashAttribute("error","Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }


        Company company = currentUser.getCompany();

        if(company == null){
            redirectAttributes.addFlashAttribute("error","Este utilizador não tem nenhuma empresa associada!");
            return getHomeRedirectUrl(currentUser);
        }

        Integer stockId = company.getStock().getId();

        List<StockItem> stockItems = stockService.getStockProducts(stockId);

        model.addAttribute("company",company);
        model.addAttribute("stockItems", stockItems);

        return "company/stock";
    }

}
