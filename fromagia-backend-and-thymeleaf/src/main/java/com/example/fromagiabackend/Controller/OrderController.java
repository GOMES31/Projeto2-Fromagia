package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.*;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import com.example.fromagiabackend.Entity.Enums.OrderState;
import com.example.fromagiabackend.Service.Invoice.InvoiceService;
import com.example.fromagiabackend.Service.Notification.NotificationService;
import com.example.fromagiabackend.Service.Order.OrderService;
import com.example.fromagiabackend.Service.Stock.StockService;
import com.example.fromagiabackend.Service.StockItem.StockItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;

    private final StockService stockService;
    private final StockItemService stockItemService;

    private final NotificationService notificationService;

    @Autowired
    public OrderController(OrderService _orderService,
                           InvoiceService _invoiceService,
                           StockService _stockService,
                           StockItemService _stockItemService,
                           NotificationService _notificationService){
        orderService = _orderService;
        invoiceService = _invoiceService;
        stockService = _stockService;
        stockItemService = _stockItemService;
        notificationService = _notificationService;
    }


    @PostMapping("/reject/{id}")
    public String rejectOrder(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");


        String authenticationResult =  handleUserAuthenticationAndPermissions(currentUser,redirectAttributes);
        if(authenticationResult != null){
            return authenticationResult;
        }

        Optional<Order> result = orderService.findById(id);

        if(result.isEmpty()){
            redirectAttributes.addFlashAttribute("message", "Encomenda não encontrada.");
            return getPendingOrdersRedirectUrl(currentUser);
        }

        Order order = result.get();

        boolean hasPermissions = checkIfUserHasRejectOrAcceptPermissions(currentUser,order);

        if(!hasPermissions){
            redirectAttributes.addFlashAttribute("message", "Não tens permissões para rejeitar esta encomenda.");
            return getPendingOrdersRedirectUrl(currentUser);
        }

        orderService.rejectOrder(order);

        redirectAttributes.addFlashAttribute("message", "Encomenda rejeitada com sucesso.");
        return getPendingOrdersRedirectUrl(currentUser);
    }


    @PostMapping("/accept/{id}")
    public String acceptOrder(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult =  handleUserAuthenticationAndPermissions(currentUser,redirectAttributes);
        if(authenticationResult != null){
            return authenticationResult;
        }

        Optional<Order> result = orderService.findById(id);

        if(result.isEmpty()){
            redirectAttributes.addFlashAttribute("message", "Encomenda não encontrada.");
            return getPendingOrdersRedirectUrl(currentUser);
        }

        Order order = result.get();
        boolean hasPermissions = checkIfUserHasRejectOrAcceptPermissions(currentUser,order);


        if(!hasPermissions){
            redirectAttributes.addFlashAttribute("message", "Não tens permissões para aceitar esta encomenda.");
            return getPendingOrdersRedirectUrl(currentUser);
        }

        orderService.acceptOrder(order);

        redirectAttributes.addFlashAttribute("message", "Encomenda aceite com sucesso.");
        return getPendingOrdersRedirectUrl(currentUser);
    }


    @GetMapping("/send/{id}")
    public String getSendOrderPage(@PathVariable Integer id,HttpSession session,Model model,RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult =  handleUserAuthenticationAndPermissions(currentUser,redirectAttributes);
        if(authenticationResult != null){
            return authenticationResult;
        }

        String sendOrdersPage = getSendOrdersPageUrl(currentUser);


        if((!Objects.equals(AccountType.EMPLOYEE,currentUser.getAccountType())) && (!Objects.equals(AccountType.SUPPLIER,currentUser.getAccountType()))){
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página.");
            return sendOrdersPage;
        }

        Optional<Order> result = orderService.findById(id);

        if(result.isPresent()){
            Order order = result.get();
            model.addAttribute("order",order);
        }

        if(Objects.equals(AccountType.EMPLOYEE,currentUser.getAccountType())){
            model.addAttribute("employee",currentUser.getEmployee());
            model.addAttribute("company",currentUser.getEmployee().getCompany());
        }

        if(Objects.equals(AccountType.SUPPLIER,currentUser.getAccountType())){
            model.addAttribute("supplier",currentUser.getSupplier());
        }


        return sendOrdersPage;
    }

    @PostMapping("/send/{id}")
    public String sendOrder(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult =  handleUserAuthenticationAndPermissions(currentUser,redirectAttributes);
        if(authenticationResult != null){
            return authenticationResult;
        }


        if((!Objects.equals(AccountType.EMPLOYEE,currentUser.getAccountType())) && (!Objects.equals(AccountType.SUPPLIER,currentUser.getAccountType()))){
            if(Objects.equals(CompanyPosition.PRODUCER,currentUser.getEmployee().getCompanyPosition())){
                redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página.");
                return "redirect:/employees/home";
            }
            return getHomeRedirectUrl(currentUser);
        }


        Optional<Order> result = orderService.findById(id);
        Order order = result.get();

        Invoice invoice = new Invoice();

        // Atualizar stock
        OrderItem orderItem = order.getOrderItems().get(0);
        Product product = orderItem.getProduct();

        if(Objects.equals(AccountType.EMPLOYEE,currentUser.getAccountType()) && Objects.nonNull(order.getClient())){
            boolean enoughStock = checkIfStockIsEnough(product,order.getCompany().getStock(), orderItem.getQuantity());
            if(!enoughStock){
                BigDecimal shortFall = shortFallStock(product,order.getCompany().getStock(),orderItem.getQuantity());
                String message = "Stock insuficiente do produto: " + product.getProductCode() + " - " + product.getProductName() +
                        "<br>Quantidade minima a produzir para concluir a encomenda: " + shortFall +
                        "<br>Por favor produzir mais para concluir a encomenda!<br><br><br><br>" +
                        "Mensagem enviada por: " + currentUser.getEmployee().getName() +
                        "<br>Cargo: " + currentUser.getEmployee().getCompanyPosition();


                notificationService.createNotification(message, currentUser.getUsername());
                return "redirect:/employees/notifications";
            }
        }
        if(Objects.equals(AccountType.SUPPLIER,currentUser.getAccountType())){
            boolean enoughStock = checkIfStockIsEnough(product,order.getSupplier().getStock(),orderItem.getQuantity());
            if(!enoughStock)
            if(Objects.equals(AccountType.SUPPLIER,currentUser.getAccountType())){
                BigDecimal shortFall = shortFallStock(product,order.getSupplier().getStock(),orderItem.getQuantity());
                redirectAttributes.addFlashAttribute("error", "Stock insuficiente! Quantidade minima a produzir do produto "
                        + product.getProductName() + ": " + shortFall);
                return "redirect:/suppliers/stock/add";
            }
        }



        invoice.setOrder(order);
        invoice.setCompany(order.getCompany());

        invoice.setProductCode(product.getProductCode());
        invoice.setProductName(product.getProductName());

        invoice.setQuantity(orderItem.getQuantity());

        invoice.setEmissionDate(LocalDateTime.now());
        invoice.setUnitPrice(product.getPrice());
        invoice.setTotalPrice(order.getTotalAmount());

        String redirectUrl = null;

        if(Objects.equals(AccountType.EMPLOYEE,currentUser.getAccountType())){
            invoice.setClient(order.getClient());
            Stock clientStock = stockService.getClientStock(order.getClient());

            List<StockItem> stockItems = clientStock.getStockItems();
            StockItem existingStockItem = null;

            for (StockItem item : stockItems) {
                if (item.getProduct().equals(product)) {
                    existingStockItem = item;
                    break;
                }
            }

            if (existingStockItem == null) {
                StockItem newItem = new StockItem();
                newItem.setQuantity(orderItem.getQuantity());
                newItem.setStock(clientStock);
                newItem.setProduct(product);
                newItem.setForSale(true);
                stockItemService.save(newItem);
            } else {
                existingStockItem.setQuantity(existingStockItem.getQuantity().add(orderItem.getQuantity()));
                stockItemService.save(existingStockItem);
            }

            StockItem companyStockItem = stockItemService.findByProductAndStock(product,order.getCompany().getStock());

            BigDecimal newQuantity = companyStockItem.getQuantity().subtract(orderItem.getQuantity());

            companyStockItem.setQuantity(newQuantity);
            stockItemService.save(companyStockItem);

            redirectUrl = "redirect:/employees/orders";
        }

        if(Objects.equals(AccountType.SUPPLIER,currentUser.getAccountType())){
            invoice.setSupplier(order.getSupplier());
            Stock companyStock = stockService.getCompanyStock(order.getCompany());

            List<StockItem> stockItems = companyStock.getStockItems();
            StockItem existingStockItem = null;

            for (StockItem item : stockItems) {
                if (item.getProduct().equals(product)) {
                    existingStockItem = item;
                    break;
                }
            }

            if (existingStockItem == null) {
                StockItem newItem = new StockItem();
                newItem.setQuantity(orderItem.getQuantity());
                newItem.setStock(companyStock);
                newItem.setProduct(product);
                newItem.setForSale(true);
                stockItemService.save(newItem);
            } else {
                existingStockItem.setQuantity(existingStockItem.getQuantity().add(orderItem.getQuantity()));
                stockItemService.save(existingStockItem);
            }


            StockItem supplierStockItem = stockItemService.findByProductAndStock(product,order.getSupplier().getStock());

            BigDecimal newQuantity = supplierStockItem.getQuantity().subtract(orderItem.getQuantity());

            supplierStockItem.setQuantity(newQuantity);
            orderService.updateOrderStateAndSaveOrder(order);
            stockItemService.save(supplierStockItem);

            redirectUrl = "redirect:/suppliers/orders";
        }

        invoiceService.save(invoice);
        redirectAttributes.addFlashAttribute("message", "Encomenda enviada com sucesso.");
        return redirectUrl;
    }

    @GetMapping("/invoices/{id}")
    public String getInvoicePage(@PathVariable Integer id,HttpSession session,RedirectAttributes redirectAttributes,Model model){
        User currentUser = (User) session.getAttribute("user");

        if(Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if(Objects.equals(AccountType.EMPLOYEE, currentUser.getAccountType()) && Objects.equals(CompanyPosition.PRODUCER,currentUser.getEmployee().getCompanyPosition())) {
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }
        Optional<Invoice> result = invoiceService.findByOrderId(id);

        if(result.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Fatura não encontrada.");
            return getHomeRedirectUrl(currentUser);
        }

        Invoice invoice = result.get();


        model.addAttribute("invoice",invoice);
        return redirectInvoicePage(currentUser);
    }


    private String getSendOrdersPageUrl(User currentUser){
        String accountType = currentUser.getAccountType().toString().toLowerCase();
        switch(currentUser.getAccountType()){
            case SUPPLIER:
                return "suppliers/send-order";
            case EMPLOYEE:
                if(Objects.equals(CompanyPosition.PRODUCER,currentUser.getEmployee().getCompanyPosition())){
                    return "redirect:/employees/home";
                }
                return "employees/send-order";
            case CLIENT:
                accountType += "s";
                return "redirect:/" + accountType + "/orders";
            case COMPANY:
                return "redirect:/" + accountType + "/orders";
            default:
                return "redirect:/auth/login";
        }
    }

    private String redirectInvoicePage(User currentUser){
        switch(currentUser.getAccountType()){
            case COMPANY:
                return "company/invoice";
            case CLIENT:
                return "clients/invoice";
            case SUPPLIER:
                return "suppliers/invoice";
            case EMPLOYEE:
                if(Objects.equals(CompanyPosition.PRODUCER,currentUser.getEmployee().getCompanyPosition())){
                    return "redirect:/employees/home";
                }
                return "employees/invoice";
            default:
                return "redirect:/auth/login";
        }
    }

    private boolean checkIfStockIsEnough(Product product, Stock stock,BigDecimal quantity){
        StockItem stockItem = stockItemService.findByProductAndStock(product,stock);

        BigDecimal result = stockItem.getQuantity().subtract(quantity);

        return result.compareTo(BigDecimal.ZERO) >= 0;

    }

    private BigDecimal shortFallStock(Product product, Stock stock,BigDecimal quantity){
        StockItem stockItem = stockItemService.findByProductAndStock(product,stock);

        return stockItem.getQuantity().subtract(quantity);
    }
    private boolean checkIfUserHasRejectOrAcceptPermissions(User currentUser, Order order){

        if(Objects.equals(AccountType.SUPPLIER, currentUser.getAccountType())){
            return checkIfObjectNotNull(order);
        }
        else if(Objects.equals(AccountType.EMPLOYEE, currentUser.getAccountType())){
            return checkIfObjectNotNull(order);
        }

        return false;
    }

    private boolean checkIfObjectNotNull(Order order){
        if(Objects.nonNull(order.getCompany()) && Objects.nonNull(order.getSupplier())){
            return true;
        }
        if(Objects.nonNull(order.getCompany()) && Objects.nonNull(order.getClient())){
            return true;
        }
        return false;

    }
    private String handleUserAuthenticationAndPermissions(User currentUser, RedirectAttributes redirectAttributes) {
        AccountType supplier = AccountType.SUPPLIER;
        AccountType employee = AccountType.EMPLOYEE;


        if(Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if((!Objects.equals(supplier, currentUser.getAccountType())) && (!Objects.equals(employee,currentUser.getAccountType()))) {
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }

        return null;
    }

    private String getPendingOrdersRedirectUrl(User user){
        switch(user.getAccountType()){
            case CLIENT:
                return "redirect:/clients/orders/pending";
            case SUPPLIER:
                return "redirect:/suppliers/orders/pending";
            case EMPLOYEE:
                return "redirect:/employees/orders/pending";
            case COMPANY:
                return "redirect:/company/orders";
            default:
                return "redirect:/auth/login";
        }
    }
}
