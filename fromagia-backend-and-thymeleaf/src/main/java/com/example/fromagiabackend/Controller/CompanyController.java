package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.*;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Helpers.EmployeeDTO;
import com.example.fromagiabackend.Service.Company.CompanyService;
import com.example.fromagiabackend.Service.Employee.EmployeeService;
import com.example.fromagiabackend.Service.ProductionHistory.ProductionHistoryService;
import com.example.fromagiabackend.Service.Stock.StockService;
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

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;
    private final StockService stockService;
    private final ProductionHistoryService productionHistoryService;

    private final SupplierService supplierService;

    private final EmployeeService employeeService;
    private final UserService userService;

    @Autowired
    public CompanyController(CompanyService _companyService,
                             StockService _stockService,
                             ProductionHistoryService _productionHistoryService,
                             SupplierService _supplierService,
                             EmployeeService _employeeService,
                             UserService _userService){

        companyService = _companyService;
        stockService = _stockService;
        productionHistoryService = _productionHistoryService;
        supplierService = _supplierService;
        employeeService = _employeeService;
        userService = _userService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        model.addAttribute("user",currentUser);
        return "company/home";
    }

    // TODO - TESTING REFACTOR ON THIS METHOD
    @GetMapping("/orders")
    public String showOrdersPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Company company = currentUser.getCompany();


        List<Order> orders = companyService.getCompanyDeliveredRejectedReceivedOrders(company.getId());

        model.addAttribute("company",company);
        model.addAttribute("orders",orders);
        return "company/orders";
    }

    @GetMapping("/stock")
    public String showStockPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Company company = currentUser.getCompany();

        Stock stock = company.getStock();

        List<StockItem> stockItems = Collections.emptyList();

        if(Objects.nonNull(stock)){
            stockItems = stockService.getStockProducts(stock.getId());
        }

        model.addAttribute("company",company);
        model.addAttribute("stockItems", stockItems);

        return "company/stock";
    }

    @GetMapping("/production-history")
    public String showProductionHistoryPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);

        if(authenticationResult != null) {
            return authenticationResult;
        }

        Company company = currentUser.getCompany();

        List<ProductionHistory> productionHistoryList = productionHistoryService.findCompanyProductHistory(company);

        model.addAttribute("company", company);
        model.addAttribute("productionHistory", productionHistoryList);

        return "company/production-history";
    }

    @GetMapping("/suppliers")
    public String showSuppliersPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");



        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Company company = currentUser.getCompany();

        List<Supplier> suppliers = supplierService.findCompanySuppliers(company);

        model.addAttribute("company", company);
        model.addAttribute("suppliers", suppliers);

        return "company/suppliers";
    }

    @GetMapping("/employees")
    public String showEmployeesPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");


        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }


        Company company = currentUser.getCompany();

        List<Employee> employees = employeeService.findCompanyEmployees(company);

        model.addAttribute("company", company);
        model.addAttribute("employees", employees);

        return "company/employees";
    }

    @GetMapping("/employees/new")
    public String showNewEmployeePage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser,redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        if (!model.containsAttribute("employeeDTO")) {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            model.addAttribute("employeeDTO", employeeDTO);
        }

        return "company/employees-new";
    }

    @PostMapping("/employees/new")
    public String addNewEmployee(@ModelAttribute("employeeDTO") @Valid EmployeeDTO employeeDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");


        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser,redirectAttributes);


        if (authenticationResult != null) {
            return authenticationResult;
        }

        if (bindingResult.hasErrors()) {
            return "company/employees-new";
        }

        User dbUser = userService.findByUsername(employeeDTO.getUsername());

        if (Objects.nonNull(dbUser)) {
            bindingResult.rejectValue("username", "error.user", "Utilizador já registado!");
            return "company/employees-new";
        }

        Company userCompany = currentUser.getCompany();

        User newUser = new User();

        newUser.setUsername(employeeDTO.getUsername());
        newUser.setPassword(employeeDTO.getPassword());
        newUser.setAccountType(AccountType.EMPLOYEE);

        Employee newEmployee = new Employee(employeeDTO.getName(), employeeDTO.getEmail(), employeeDTO.getSalary(), employeeDTO.getCompanyPosition());
        newEmployee.setUser(newUser);
        newEmployee.setCompany(userCompany);

        newUser.setEmployee(newEmployee);
        userService.save(newUser);

        employeeService.save(newEmployee);
        companyService.save(userCompany);

        redirectAttributes.addFlashAttribute("message", "Funcionário adicionado com sucesso!");

        session.removeAttribute("employeeDTO");

        return "redirect:/company/employees";
    }

    private String handleUserAuthenticationAndPermissions(User currentUser, RedirectAttributes redirectAttributes) {

        if (Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if (!Objects.equals(AccountType.COMPANY, currentUser.getAccountType())) {
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }

        Company company = currentUser.getCompany();

        if(company == null){
            redirectAttributes.addFlashAttribute("error","Este utilizador não tem nenhuma empresa associada!");
            return getHomeRedirectUrl(currentUser);
        }

        return null;
    }


}
