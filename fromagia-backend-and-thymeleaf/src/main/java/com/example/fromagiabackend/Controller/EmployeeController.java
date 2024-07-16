package com.example.fromagiabackend.Controller;


import com.example.fromagiabackend.Entity.*;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import com.example.fromagiabackend.Entity.Helpers.OrderDTO;
import com.example.fromagiabackend.Entity.Helpers.StockItemDTO;
import com.example.fromagiabackend.Entity.Helpers.UpdateStockDTO;
import com.example.fromagiabackend.Service.Company.CompanyService;
import com.example.fromagiabackend.Service.Notification.NotificationService;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final ProductService productService;

    private final CompanyService companyService;

    private final StockService stockService;

    private final ProductionHistoryService productionHistoryService;

    private final SupplierService supplierService;

    private final StockItemService stockItemService;

    private final OrderService orderService;

    private final NotificationService notificationService;
    @Autowired
    public EmployeeController(ProductService _productService,
                              CompanyService _companyService,
                              StockService _stockService,
                              ProductionHistoryService _productionHistoryService,
                              SupplierService _supplierService,
                              StockItemService _stockItemService,
                              OrderService _orderService,
                              NotificationService _notificationService){
        productService = _productService;
        companyService = _companyService;
        stockService = _stockService;
        productionHistoryService = _productionHistoryService;
        supplierService = _supplierService;
        stockItemService = _stockItemService;
        orderService = _orderService;
        notificationService = _notificationService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        model.addAttribute("user",currentUser);
        model.addAttribute("employee",currentUser.getEmployee());
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


        Company employeeCompany = currentUser.getEmployee().getCompany();
        Employee employee = currentUser.getEmployee();

        List<Order> orders = companyService.getCompanyCompletedRejectedOrders(employeeCompany.getId());

        model.addAttribute("employee",employee);
        model.addAttribute("orders",orders);

        return "employees/orders";
    }




    @GetMapping("/stock")
    public String showStockPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Company company = currentUser.getEmployee().getCompany();
        Employee employee = currentUser.getEmployee();
        Stock stock = company.getStock();

        List<StockItem> stockItems = Collections.emptyList();

        if(Objects.nonNull(stock)){
            stockItems = stockService.getStockProducts(stock.getId());
        }

        model.addAttribute("employee",employee);
        model.addAttribute("stockItems", stockItems);

        return "employees/stock";
    }

    @GetMapping("/notifications")
    public String getNotificationsPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if(authenticationResult != null) {
            return authenticationResult;
        }

        List<Notification> notifications = notificationService.findAll();

        model.addAttribute("notifications",notifications);
        model.addAttribute("employee",currentUser.getEmployee());

        return "employees/notifications";
    }

    @GetMapping("/notifications/new")
    public String getNewNotificationPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if(authenticationResult != null) {
            return authenticationResult;
        }

        List<Notification> notifications = notificationService.findAll();

        model.addAttribute("notifications",notifications);
        model.addAttribute("employee",currentUser.getEmployee());

        return "employees/notifications";
    }

    @PostMapping("/notifications/send")
    public String sendNotification(@RequestParam("message") String message,HttpSession session,RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if(authenticationResult != null) {
            return authenticationResult;
        }

        String createdBy = currentUser.getUsername();

        message += "\n\n\n\nMensagem enviada por: " + currentUser.getEmployee().getName() +
                    "\nCargo: " + currentUser.getEmployee().getCompanyPosition();

        notificationService.createNotification(message, createdBy);

        redirectAttributes.addFlashAttribute("message", "Notificação enviada com sucesso!");

        return "redirect:/employees/notifications";
    }

    @GetMapping("/production-history")
    public String showProductionHistoryPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);

        if(authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.PRODUCER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if( employeePermissionsResult != null){
            return employeePermissionsResult;
        }

        Company company = currentUser.getEmployee().getCompany();
        Employee employee = currentUser.getEmployee();

        List<ProductionHistory> productionHistoryList = productionHistoryService.findCompanyProductHistory(company);

        model.addAttribute("employee", employee);
        model.addAttribute("productionHistory", productionHistoryList);

        return "employees/production-history";
    }

    @GetMapping("/suppliers")
    public String showSuppliersPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
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

        Company company = currentUser.getEmployee().getCompany();
        Employee employee = currentUser.getEmployee();

        List<Supplier> suppliers = supplierService.findCompanySuppliers(company);

        model.addAttribute("employee", employee);
        model.addAttribute("suppliers", suppliers);

        return "employees/suppliers";
    }
    @GetMapping("/products/add")
    public String showNewProductPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){

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

        if (!model.containsAttribute("stockItemDTO")) {
            StockItemDTO stockItemDTO = new StockItemDTO();
            model.addAttribute("stockItemDTO",stockItemDTO);
        }

        Employee employee = currentUser.getEmployee();
        model.addAttribute("employee", employee);
        return "employees/add-products";
    }

    @PostMapping("/products/add")
    public String addNewProduct(@ModelAttribute("stockItemDTO") @Valid StockItemDTO stockItemDTO,BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes){
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

        if (bindingResult.hasErrors()) {
            return "employees/add-products";
        }

        Product product = productService.getProductByProductName(stockItemDTO.getProductName());
        if (Objects.nonNull(product)) {
            bindingResult.rejectValue("productName", "error.productName", "Já existe um produto com esse nome!");
            return "employees/add-products";
        }

        Product newProduct = new Product();

        newProduct.setProductName(stockItemDTO.getProductName());
        newProduct.setPrice(stockItemDTO.getPrice());
        newProduct.setProductType(stockItemDTO.getProductType());

        productService.save(newProduct);

        Company company = currentUser.getEmployee().getCompany();

        Stock stock = stockService.getCompanyStock(company);

        StockItem stockItem = new StockItem();
        stockItem.setProduct(newProduct);
        stockItem.setStock(stock);
        stockItem.setQuantity(stockItemDTO.getQuantity());


        boolean forSale = stockItemDTO.getForSale();
        stockItem.setForSale(forSale);

        stockItemService.save(stockItem);

        stock.getStockItems().add(stockItem);
        stock.setLastUpdate(LocalDateTime.now());

        stockService.save(stock);

        redirectAttributes.addFlashAttribute("message", "Produto adicionado com sucesso!");

        session.removeAttribute("stockItemDTO");

        return "redirect:/employees/stock";

    }


    @GetMapping("/suppliers/find")
    public String showSuppliersAvailablePage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if(authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.MANAGER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if(employeePermissionsResult != null){
            return employeePermissionsResult;
        }
        Employee employee = currentUser.getEmployee();

        List<Supplier> suppliers = supplierService.findSuppliersWithoutCompany();

        model.addAttribute("employee", employee);
        model.addAttribute("suppliers",suppliers);
        return "employees/find-suppliers";
    }


    @GetMapping("/suppliers/stock/{id}")
    public String showSuppliersStockItems(Model model, HttpSession session, RedirectAttributes redirectAttributes, @PathVariable Integer id){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if(authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.MANAGER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if(employeePermissionsResult != null){
            return employeePermissionsResult;
        }

        Optional<Supplier> result = supplierService.findById(id);
        Supplier supplier;
        if (result.isPresent()) {
            supplier = result.get();
        }
        else
        {
            redirectAttributes.addFlashAttribute("error","Erro inesperado aconteceu!");
            return getHomeRedirectUrl(currentUser);
        }

        Stock stock = stockService.getSupplierStock(supplier);

        if(Objects.isNull(stock)){
            redirectAttributes.addFlashAttribute("error","Erro inesperado aconteceu!");
            return getHomeRedirectUrl(currentUser);
        }
        List<StockItem> stockItems = stock.getStockItems().stream().filter(StockItem::getForSale).collect(Collectors.toList());

        Employee employee = currentUser.getEmployee();

        model.addAttribute("supplier",supplier);
        model.addAttribute("employee", employee);
        model.addAttribute("stockItems",stockItems);
        return "employees/suppliers-products";
    }


    @GetMapping("/orders/new")
    public String showNewOrderPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){

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

        String suppliersResult = handleSuppliersList(currentUser,redirectAttributes);
        if(suppliersResult != null){
            return suppliersResult;
        }

        Employee employee = currentUser.getEmployee();

        Company company = employee.getCompany();

        List<Supplier> suppliers = supplierService.findCompanySuppliers(company);

        model.addAttribute("employee", employee);
        model.addAttribute("suppliers",suppliers);


        return "employees/new-order";
    }


    @PostMapping("/orders/new")
    public String addSupplierAndRedirectOrdersProductPage(@RequestParam("supplierId") Integer supplierId,Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.MANAGER;
        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if (employeePermissionsResult != null) {
            return employeePermissionsResult;
        }

        Optional<Supplier> result = supplierService.findById(supplierId);
        Supplier supplier;
        if (result.isPresent()) {
            supplier = result.get();
        }
        else
        {
            redirectAttributes.addFlashAttribute("error","Erro inesperado aconteceu!");
            return getHomeRedirectUrl(currentUser);
        }

        List<StockItem> stockItems = stockService.getSupplierStock(supplier).getStockItems().stream().filter(StockItem::getForSale).collect(Collectors.toList());

        model.addAttribute("stockItems",stockItems);
        model.addAttribute("supplierId",supplierId);

        if (!model.containsAttribute("orderDTO")) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setSupplierId(supplierId);
            model.addAttribute("orderDTO",orderDTO);
        }

        return "employees/new-order-product";
    }

    @PostMapping("/orders/send")
    public String sendOrder(@Valid @ModelAttribute("orderDTO")OrderDTO orderDTO, BindingResult bindingResult,HttpSession session,Model model,RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.MANAGER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if(employeePermissionsResult != null){
            return employeePermissionsResult;
        }

        if (bindingResult.hasErrors()) {
            Supplier supplier = supplierService.findById(orderDTO.getSupplierId()).orElse(null);

            if (supplier != null) {
                List<StockItem> stockItems = stockService.getSupplierStock(supplier)
                        .getStockItems()
                        .stream()
                        .filter(StockItem::getForSale)
                        .collect(Collectors.toList());
                model.addAttribute("stockItems", stockItems);
            }
            model.addAttribute("supplier",supplier);
            model.addAttribute("orderDTO", orderDTO);
            return "employees/new-order-product";
        }

        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        Company company = currentUser.getEmployee().getCompany();

        String orderCode = orderService.generateOrderCode();

        StockItem stockItem = stockItemService.findById(orderDTO.getStockItemId());
        orderItem.setProduct(stockItem.getProduct());
        orderItem.setQuantity(orderDTO.getQuantity());

        order.addOrderItem(orderItem);
        order.setCompany(company);


        Supplier supplier = supplierService.findById(orderDTO.getSupplierId()).orElse(null);

        order.setSupplier(supplier);
        order.setSentDate(LocalDateTime.now());
        order.setOrderCode(orderCode);

        BigDecimal price = stockItem.getProduct().getPrice();
        BigDecimal quantity = orderDTO.getQuantity();
        BigDecimal totalAmount = price.multiply(quantity);


        order.setTotalAmount(totalAmount);

        List<Order> companyOrders = companyService.getCompanyOrders(company.getId());
        companyOrders.add(order);

        List<Order> supplierOrders = supplierService.getSupplierOrders(supplier.getId());
        supplierOrders.add(order);

        orderService.save(order);

        return "redirect:/employees/orders/pending";
    }

    @GetMapping("/orders/pending")
    public String showPendingOrdersPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.MANAGER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if(employeePermissionsResult != null){
            return employeePermissionsResult;
        }

        Company employeeCompany = currentUser.getEmployee().getCompany();
        Employee employee = currentUser.getEmployee();

        List<Order> orders = companyService.getCompanyPendingOrAcceptedOrders(employeeCompany.getId());

        model.addAttribute("employee",employee);
        model.addAttribute("orders",orders);

        return "employees/pending-orders";
    }

    @GetMapping("/production/new")
    public String showNewProductionPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){

        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.PRODUCER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if(employeePermissionsResult != null){
            return employeePermissionsResult;
        }

        if (!model.containsAttribute("updateStockDTO")) {
            UpdateStockDTO updateStockDTO = new UpdateStockDTO();
            model.addAttribute("updateStockDTO",updateStockDTO);
        }

        Employee employee = currentUser.getEmployee();
        Company company = currentUser.getEmployee().getCompany();
        List<StockItem> stockItems = stockService.getCompanyStock(company).getStockItems();

        model.addAttribute("stockItems",stockItems);
        model.addAttribute("employee",employee);
        return "employees/new-production";
    }


    @PostMapping("/suppliers/add/{id}")
    public String addSupplierToList(HttpSession session, RedirectAttributes redirectAttributes, @PathVariable Integer id){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.MANAGER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if(employeePermissionsResult != null){
            return employeePermissionsResult;
        }

        Optional<Supplier> result = supplierService.findById(id);
        Supplier supplier;
        if (result.isPresent()) {
            supplier = result.get();
        }
        else
        {
            redirectAttributes.addFlashAttribute("error","Erro inesperado aconteceu!");
            return getHomeRedirectUrl(currentUser);
        }

        Company company = currentUser.getEmployee().getCompany();
        supplier.setCompany(company);

        supplierService.save(supplier);

        redirectAttributes.addFlashAttribute("message", "Fornecedor contratado com sucesso!");
        return "redirect:/employees/suppliers";
    }
    @PostMapping ("/production/new")
    public String addNewProduction(@ModelAttribute("updateStockDTO") @Valid UpdateStockDTO updateStockDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        CompanyPosition positionAllowed = CompanyPosition.PRODUCER;

        String employeePermissionsResult = handleEmployeePermissions(currentUser, redirectAttributes, positionAllowed);
        if(employeePermissionsResult != null){
            return employeePermissionsResult;
        }

        if (bindingResult.hasErrors()) {
            return "employees/new-production";
        }

        StockItem stockItem = stockItemService.findById(updateStockDTO.getStockItemId());
        BigDecimal currentQuantity = stockItem.getQuantity();
        BigDecimal dtoQuantity = updateStockDTO.getQuantity();

        BigDecimal sum = currentQuantity.add(dtoQuantity);
        stockItem.setQuantity(sum);

        stockItemService.save(stockItem);

        ProductionHistory productionHistory = new ProductionHistory();


        productionHistory.setProduct(stockItem.getProduct());
        productionHistory.setProductionDate(LocalDateTime.now());
        productionHistory.setQuantityProduced(dtoQuantity);


        Optional<Company> result = companyService.findById(currentUser.getEmployee().getCompany().getId());
        if (result.isPresent()) {
            Company company = result.get();
            company.addProductionHistory(productionHistory);

            companyService.save(company);
        }
        else{
            redirectAttributes.addFlashAttribute("error","Este utilizador não tem nenhuma empresa associada!");
            return getHomeRedirectUrl(currentUser);
        }



        redirectAttributes.addFlashAttribute("message", "Produção realizada com sucesso e stock atualizado com sucesso!");
        return "redirect:/employees/stock";
    }


    private String handleEmployeePermissions(User currentUser, RedirectAttributes redirectAttributes, CompanyPosition companyPosition) {

        if(!Objects.equals(companyPosition,currentUser.getEmployee().getCompanyPosition())){
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }

        return null;
    }

    private String handleSuppliersList(User currentUser, RedirectAttributes redirectAttributes){

        Company company = currentUser.getEmployee().getCompany();

        List<Supplier> suppliers = supplierService.findCompanySuppliers(company);

        if(suppliers.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Nenhum fornecedor registado! Pesquise por fornecedores!");
            return "redirect:/employees/suppliers/find";
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
