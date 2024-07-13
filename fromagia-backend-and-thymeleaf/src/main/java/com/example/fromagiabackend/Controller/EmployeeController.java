package com.example.fromagiabackend.Controller;


import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Employee;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Entity.User;
import com.example.fromagiabackend.Service.Company.CompanyService;
import com.example.fromagiabackend.Service.Product.ProductService;
import com.example.fromagiabackend.Service.User.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final UserService userService;
    private final ProductService productService;

    private final CompanyService companyService;

    @Autowired
    public EmployeeController(UserService _userService,
                              ProductService _productService,
                              CompanyService _companyService){
        userService = _userService;
        productService = _productService;
        companyService = _companyService;
    }
    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        model.addAttribute("user",currentUser);
        return "employees/home";
    }


    @GetMapping("/orders")
    public String showOrdersPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.MANAGER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if( employeePermissionsResult != null){
            return employeePermissionsResult;
        }


        Employee employee = currentUser.getEmployee();

        Company employeeCompany = employee.getCompany();

        List<Order> orders = companyService.getCompanyOrders(employeeCompany.getId());

        model.addAttribute("employee",employee);
        model.addAttribute("orders",orders);

        return "employees/orders";
    }

    private String handleEmployeePermissions(User currentUser, RedirectAttributes redirectAttributes, CompanyPosition companyPosition) {

        CompanyPosition userPosition = currentUser.getEmployee().getCompanyPosition();

        if(!Objects.equals(userPosition,companyPosition)){
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }

        return null;
    }


    private String handleUserAuthenticationAndPermissions(User currentUser, RedirectAttributes redirectAttributes) {

        if (Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if(!Objects.equals(AccountType.EMPLOYEE, currentUser.getAccountType())) {
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }

        Company company = currentUser.getEmployee().getCompany();

        if(company == null){
            redirectAttributes.addFlashAttribute("error","Este utilizador não tem nenhuma empresa associada!");
            return getHomeRedirectUrl(currentUser);
        }

        return null;
    }
}
