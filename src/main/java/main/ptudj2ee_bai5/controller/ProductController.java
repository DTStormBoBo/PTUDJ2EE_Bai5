package main.ptudj2ee_bai5.controller;

import jakarta.validation.Valid;
import main.ptudj2ee_bai5.model.*;
import main.ptudj2ee_bai5.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String Root() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String Index(Model model) {
        model.addAttribute("listproduct", productService.getAllProducts());
        return "products/list";
    }

    @GetMapping("/products/add")
    public String Add(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/add";
    }

    @PostMapping("/products/save")
    public String Save(@Valid Product newProduct, BindingResult result,
                         @RequestParam(value = "image", required = false) MultipartFile image, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", newProduct);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add";
        }
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                newProduct.setImage(fileName);
            } catch (Exception e) {
                model.addAttribute("product", newProduct);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("errorMessage", "Lỗi khi tải ảnh: " + e.getMessage());
                return "products/add";
            }
        }
        productService.saveProduct(newProduct);
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String Edit(@PathVariable int id, Model model) {
        Product find = productService.getProductById(id);
        if (find == null) {
            return "redirect:/products"; // Product not found, redirect to list
        }
        model.addAttribute("product", find);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/edit";
    }

    @GetMapping("/products/edit")
    public String EditFallback() {
        // Fallback: if user access /products/edit without ID, redirect to products list
        return "redirect:/products";
    }

    @PostMapping("/products/edit")
    public String Edit(@Valid Product editProduct,
                       BindingResult result,
                       @RequestParam(value = "image", required = false) MultipartFile image,
                       Model model) {
        try {
            System.out.println("=== EDIT POST ===");
            System.out.println("Product ID: " + editProduct.getId());
            System.out.println("Product Name: " + editProduct.getName());
            System.out.println("Category: " + editProduct.getCategory());
            System.out.println("Has errors: " + result.hasErrors());

            // Validate product ID exists
            if (editProduct.getId() <= 0) {
                System.out.println("Invalid product ID");
                return "redirect:/products";
            }

            // If validation failed, show form with errors
            if (result.hasErrors()) {
                System.out.println("VALIDATION ERRORS:");
                result.getAllErrors().forEach(e -> System.out.println("  - " + e.getDefaultMessage()));
                model.addAttribute("product", editProduct);
                model.addAttribute("categories", categoryService.getAllCategories());
                return "products/edit";
            }

            // Process image upload if provided
            if (image != null && !image.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    editProduct.setImage(fileName);
                } catch (Exception imgEx) {
                    System.out.println("Image upload error: " + imgEx.getMessage());
                    model.addAttribute("product", editProduct);
                    model.addAttribute("categories", categoryService.getAllCategories());
                    model.addAttribute("errorMessage", "Lỗi khi tải ảnh: " + imgEx.getMessage());
                    return "products/edit";
                }
            }

            System.out.println("Updating product with ID: " + editProduct.getId());
            productService.saveProduct(editProduct);
            System.out.println("Update successful, redirecting to /products");
            return "redirect:/products";

        } catch (Exception e) {
            System.out.println("FATAL ERROR in edit POST: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("product", editProduct);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "products/edit";
        }
    }

    @GetMapping("/products/delete/{id}")
    public String Delete(@PathVariable int id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
} 