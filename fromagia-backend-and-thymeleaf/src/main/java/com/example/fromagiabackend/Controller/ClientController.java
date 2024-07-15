package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.*;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import com.example.fromagiabackend.Service.Client.ClientService;
import com.example.fromagiabackend.Service.Company.CompanyService;
import com.example.fromagiabackend.Service.Product.ProductService;
import com.example.fromagiabackend.Service.ProductionHistory.ProductionHistoryService;
import com.example.fromagiabackend.Service.Stock.StockService;
import com.example.fromagiabackend.Service.StockItem.StockItemService;
import com.example.fromagiabackend.Service.Supplier.SupplierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ProductService productService;

    private final ClientService clientService;

    private final StockService stockService;


    private final StockItemService stockItemService;

    @Autowired
    public ClientController(ProductService _productService,
                            ClientService _clientService,
                            StockService _stockService,
                            StockItemService _stockItemService){
        productService = _productService;
        clientService = _clientService;
        stockService = _stockService;
        stockItemService = _stockItemService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        model.addAttribute("user",currentUser);
        model.addAttribute("client",currentUser.getClient());
        return "clients/home";
    }

    @GetMapping("/stock")
    public String showStockPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Stock stock = currentUser.getClient().getStock();
        Client client = currentUser.getClient();

        List<StockItem> stockItems = Collections.emptyList();

        if(Objects.nonNull(stock)){
            stockItems = stockService.getStockProducts(stock.getId());
        }

        model.addAttribute("client",client);
        model.addAttribute("stockItems", stockItems);

        return "clients/stock";
    }

    @GetMapping("/orders")
    public String showOrdersPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Client client = currentUser.getClient();
        List<Order> orders = clientService.getClientAcceptedOrRejectedOrders(client.getId());

        model.addAttribute("client",client);
        model.addAttribute("orders",orders);

        return "clients/orders";
    }

    private String handleUserAuthenticationAndPermissions(User currentUser, RedirectAttributes redirectAttributes) {

        if (Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if(!Objects.equals(AccountType.CLIENT, currentUser.getAccountType())) {
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }

        Client client = currentUser.getClient();

        if(client == null){
            redirectAttributes.addFlashAttribute("error","Este utilizador não tem nenhum cliente associado!");
            return getHomeRedirectUrl(currentUser);
        }

        return null;
    }
}
