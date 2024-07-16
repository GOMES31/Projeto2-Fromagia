package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Entity.Supplier;
import com.example.fromagiabackend.Entity.User;
import com.example.fromagiabackend.Service.Order.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.Optional;

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService _orderService){
        orderService = _orderService;
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

    public static String getPendingOrdersRedirectUrl(User user){
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
