package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.service.IproductService;

import java.util.List;

@RequestMapping("Product")
@RestController
@AllArgsConstructor
public class RestControllerProduct {
    private IproductService iproductService;
    @PostMapping("addprodcut")
    public Product addProduct( @RequestBody Product product) {
        return iproductService.addProduct(product);
    }

    @PutMapping("update")
    public Product updateProduct( @RequestBody Product product) {
        return iproductService.updateProduct(product) ;
    }

    @GetMapping("get/{id}")
    public Product getProductById( @PathVariable Long id) {
        return iproductService.getProductById(id);
    }

   @GetMapping("getall")
    public List<Product> getAllProducts() {
        return iproductService.getAllProducts();
    }
    @DeleteMapping("delete")
    public void deleteProduct(@RequestBody Product product) {
        iproductService.deleteProduct(product);

    }

}
