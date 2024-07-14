package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.*;
import com.example.fromagiabackend.Entity.Enums.AccountType;
import com.example.fromagiabackend.Entity.Enums.CompanyPosition;
import com.example.fromagiabackend.Entity.Helpers.StockItemDTO;
import com.example.fromagiabackend.Entity.Helpers.UpdateStockDTO;
import com.example.fromagiabackend.Service.Product.ProductService;
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

import static com.example.fromagiabackend.Controller.AppController.getHomeRedirectUrl;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    private final StockService stockService;

    private final ProductService productService;

    private final StockItemService stockItemService;

    @Autowired
    public SupplierController(SupplierService _supplierService,
                              StockService _stockService,
                              ProductService _productService,
                              StockItemService _stockItemService){
        supplierService = _supplierService;
        stockService = _stockService;
        productService = _productService;
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
        model.addAttribute("supplier",currentUser.getSupplier());
        return "suppliers/home";
    }

    @GetMapping("/stock")
    public String showStockPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        Stock stock = currentUser.getSupplier().getStock();
        Supplier supplier = currentUser.getSupplier();

        List<StockItem> stockItems = Collections.emptyList();

        if(Objects.nonNull(stock)){
            stockItems = stockService.getStockProducts(stock.getId());
        }

        model.addAttribute("supplier",supplier);
        model.addAttribute("stockItems", stockItems);

        return "suppliers/stock";
    }

    @GetMapping("/products/add")
    public String showNewProductPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){

        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        if (!model.containsAttribute("stockItemDTO")) {
            StockItemDTO stockItemDTO = new StockItemDTO();
            model.addAttribute("stockItemDTO",stockItemDTO);
        }

        Supplier supplier = currentUser.getSupplier();
        model.addAttribute("supplier",supplier);
        return "suppliers/add-products";
    }

    @PutMapping("/products/update")
    public String addNewProduct(@ModelAttribute("stockItemDTO") @Valid StockItemDTO stockItemDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }


        if (bindingResult.hasErrors()) {
            return "suppliers/add-products";
        }


        Product product = productService.getProductByProductName(stockItemDTO.getProductName());
        if (Objects.nonNull(product)) {
            bindingResult.rejectValue("productName", "error.productName", "Já existe um produto com esse nome!");
            return "employees/add-products";
        }

        Product newProduct = new Product();

        newProduct.setProductName(stockItemDTO.getProductName());
        newProduct.setPrice(stockItemDTO.getPrice());

        productService.save(newProduct);

        Supplier supplier = currentUser.getSupplier();

        Stock stock = stockService.getSupplierStock(supplier);

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

        return "redirect:/suppliers/stock";

    }

    @GetMapping("/stock/add")
    public String showAddStockPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){

        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }

        if (!model.containsAttribute("updateStockDTO")) {
            UpdateStockDTO updateStockDTO = new UpdateStockDTO();
            model.addAttribute("updateStockDTO",updateStockDTO);
        }

        Supplier supplier = currentUser.getSupplier();
        List<StockItem> stockItems = stockService.getSupplierStock(supplier).getStockItems();


        model.addAttribute("stockItems",stockItems);
        model.addAttribute("supplier",supplier);
        return "suppliers/add-stock";
    }

    @PostMapping ("/stock/update")
    public String updateProductStock(@ModelAttribute("updateStockDTO") @Valid UpdateStockDTO updateStockDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");

        String authenticationResult = handleUserAuthenticationAndPermissions(currentUser, redirectAttributes);
        if (authenticationResult != null) {
            return authenticationResult;
        }


        if (bindingResult.hasErrors()) {
            return "suppliers/add-stock";
        }

        StockItem stockItem = stockItemService.findById(updateStockDTO.getStockItemId());
        BigDecimal currentQuantity = stockItem.getQuantity();
        BigDecimal dtoQuantity = updateStockDTO.getQuantity();

        BigDecimal sum = currentQuantity.add(dtoQuantity);
        stockItem.setQuantity(sum);

        stockItemService.save(stockItem);

        redirectAttributes.addFlashAttribute("message", "Stock atualizado com sucesso!");
        return "redirect:/suppliers/stock";
    }

    private String handleUserAuthenticationAndPermissions(User currentUser, RedirectAttributes redirectAttributes) {

        if (Objects.isNull(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Tens que fazer login para poder aceder ao conteúdo!");
            return "redirect:/auth/login";
        }

        if(!Objects.equals(AccountType.SUPPLIER, currentUser.getAccountType())) {
            redirectAttributes.addFlashAttribute("error", "Não tens permissões para aceder a esta página!");
            return getHomeRedirectUrl(currentUser);
        }

        Supplier supplier = currentUser.getSupplier();

        if(supplier == null){
            redirectAttributes.addFlashAttribute("error","Este utilizador não tem nenhum fornecedor associado!");
            return getHomeRedirectUrl(currentUser);
        }

        return null;
    }

}
