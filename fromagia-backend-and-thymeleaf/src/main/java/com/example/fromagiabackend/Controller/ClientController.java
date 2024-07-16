package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.*;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import com.example.fromagiabackend.Entity.Helpers.OrderDTO;
import com.example.fromagiabackend.Service.Client.ClientService;
import com.example.fromagiabackend.Service.Company.CompanyService;
import com.example.fromagiabackend.Service.Order.OrderService;
import com.example.fromagiabackend.Service.Product.ProductService;
import com.example.fromagiabackend.Service.ProductionHistory.ProductionHistoryService;
import com.example.fromagiabackend.Service.Stock.StockService;
import com.example.fromagiabackend.Service.StockItem.StockItemService;
import com.example.fromagiabackend.Service.Supplier.SupplierService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final CompanyService companyService;
    private final ClientService clientService;

    private final StockService stockService;
    private final StockItemService stockItemService;

    private final OrderService orderService;

    @Autowired
    public ClientController(CompanyService _companyService,
                            ClientService _clientService,
                            StockService _stockService,
                            StockItemService _stockItemService,
                            OrderService _orderService){
        companyService = _companyService;
        clientService = _clientService;
        stockService = _stockService;
        stockItemService = _stockItemService;
        orderService = _orderService;
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
        List<Order> orders = clientService.getClientDeliveredRejectedReceivedOrders(client.getId());

        model.addAttribute("client",client);
        model.addAttribute("orders",orders);

        return "clients/orders";
    }

    @GetMapping("/orders/new")
    public String showNewOrderPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){

        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }


        List<Company> companies = companyService.findAllCompanies();

        Client client = currentUser.getClient();

        model.addAttribute("client", client);
        model.addAttribute("companies",companies);


        return "clients/new-order";
    }

    @PostMapping("/orders/new")
    public String addSupplierAndRedirectOrdersProductPage(@RequestParam("companyId") Integer companyId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Optional<Company> result = companyService.findById(companyId);
        Company company;
        if (result.isPresent()) {
            company = result.get();
        }
        else
        {
            redirectAttributes.addFlashAttribute("error","Erro inesperado aconteceu!");
            return getHomeRedirectUrl(currentUser);
        }

        List<StockItem> stockItems = stockService.getCompanyStock(company).getStockItems().stream().filter(StockItem::getForSale).collect(Collectors.toList());

        model.addAttribute("stockItems",stockItems);
        model.addAttribute("companyId",companyId);

        if (!model.containsAttribute("orderDTO")) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setCompanyId(companyId);
            model.addAttribute("orderDTO",orderDTO);
        }

        return "clients/new-order-product";
    }

    @PostMapping("/orders/send")
    public String sendOrder(@Valid @ModelAttribute("orderDTO")OrderDTO orderDTO, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        if (bindingResult.hasErrors()) {
            Company company = companyService.findById(orderDTO.getCompanyId()).orElse(null);

            if(company != null){
                List<StockItem> stockItems = stockService.getCompanyStock(company)
                        .getStockItems()
                        .stream()
                        .filter(StockItem::getForSale)
                        .collect(Collectors.toList());
                model.addAttribute("stockItems", stockItems);
            }

            model.addAttribute("company",company);
            model.addAttribute("orderDTO", orderDTO);
            return "clients/new-order-product";
        }

        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        Client client = currentUser.getClient();

        String orderCode = orderService.generateOrderCode();

        StockItem stockItem = stockItemService.findById(orderDTO.getStockItemId());
        orderItem.setProduct(stockItem.getProduct());
        orderItem.setQuantity(orderDTO.getQuantity());

        order.addOrderItem(orderItem);
        order.setClient(client);


        Company company = companyService.findById(orderDTO.getCompanyId()).orElse(null);

        order.setCompany(company);
        order.setSentDate(LocalDateTime.now());
        order.setOrderCode(orderCode);

        BigDecimal price = stockItem.getProduct().getPrice();
        BigDecimal quantity = orderDTO.getQuantity();
        BigDecimal totalAmount = price.multiply(quantity);


        order.setTotalAmount(totalAmount);

        List<Order> companyOrders = companyService.getCompanyOrders(company.getId());
        companyOrders.add(order);

        List<Order> clientOrders = clientService.getClientOrders(client.getId());
        clientOrders.add(order);

        orderService.save(order);

        return "redirect:/clients/orders/pending";
    }
    @GetMapping("/orders/pending")
    public String showPendingOrdersPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Client client = currentUser.getClient();

        List<Order> orders = clientService.getClientPendingOrAcceptedOrders(client.getId());

        model.addAttribute("client",client);
        model.addAttribute("orders",orders);

        return "clients/pending-orders";
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
