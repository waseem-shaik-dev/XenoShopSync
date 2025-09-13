package com.example.XenoShopSync.service.scheduler;


import com.example.XenoShopSync.service.CustomerService;
import com.example.XenoShopSync.service.OrderService;
import com.example.XenoShopSync.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class ShopifySyncScheduler {

    private final CustomerService customerService;
    private final OrderService orderService;
    private final ProductService productService;

    public ShopifySyncScheduler(CustomerService customerService,
                                OrderService orderService,
                                ProductService productService) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.productService = productService;
    }

    //@Scheduled(cron = "0 */30 * * * *")
    public void syncAllTenants() {
        customerService.scheduledSync();
        orderService.scheduledSync();
        productService.scheduledSync();
    }
}
