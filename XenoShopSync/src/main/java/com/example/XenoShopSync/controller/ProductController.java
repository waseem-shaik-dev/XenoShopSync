package com.example.XenoShopSync.controller;



import com.example.XenoShopSync.dto.ProductDto;
import com.example.XenoShopSync.repository.TenantRepository;
import com.example.XenoShopSync.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService,TenantRepository tenantRepository) {
        this.productService = productService;

    }

    // ✅ Get all products for a tenant
    @GetMapping("/{tenantId}")
    public ResponseEntity<List<ProductDto>> getAllProducts(@PathVariable String tenantId) {
        List<ProductDto> products = productService.getAllProducts(tenantId);
        return ResponseEntity.ok(products);
    }

    // ✅ Get product by database ID
    @GetMapping("/id/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Optional<ProductDto> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get product by Shopify ID
    @GetMapping("/{tenantId}/shopify/{shopifyId}")
    public ResponseEntity<ProductDto> getProductByShopifyId(@PathVariable String tenantId,
                                                            @PathVariable Long shopifyId) {
        Optional<ProductDto> product = productService.getProductByShopifyId(tenantId, shopifyId);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get products by vendor
    @GetMapping("/{tenantId}/vendor/{vendor}")
    public ResponseEntity<List<ProductDto>> getProductsByVendor(@PathVariable String tenantId,
                                                                @PathVariable String vendor) {
        List<ProductDto> products = productService.getProductsByVendor(tenantId, vendor);
        return ResponseEntity.ok(products);
    }

    // ✅ Get products by product type
    @GetMapping("/{tenantId}/type/{productType}")
    public ResponseEntity<List<ProductDto>> getProductsByType(@PathVariable String tenantId,
                                                              @PathVariable String productType) {
        List<ProductDto> products = productService.getProductsByType(tenantId, productType);
        return ResponseEntity.ok(products);
    }

    // ✅ Get products by tag
    @GetMapping("/{tenantId}/tag/{tag}")
    public ResponseEntity<List<ProductDto>> getProductsByTag(@PathVariable String tenantId,
                                                             @PathVariable String tag) {
        List<ProductDto> products = productService.getProductsByTag(tenantId, tag);
        return ResponseEntity.ok(products);
    }

    // ✅ Count products for a tenant
    @GetMapping("/{tenantId}/count")
    public ResponseEntity<Long> countProductsByTenant(@PathVariable String tenantId) {
        Long count = productService.countProductsByTenant(tenantId);
        return ResponseEntity.ok(count);
    }



}
