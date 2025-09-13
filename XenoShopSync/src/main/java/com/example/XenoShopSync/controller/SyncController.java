package com.example.XenoShopSync.controller;

import com.example.XenoShopSync.service.scheduler.ShopifySyncScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tenant/sync")
public class SyncController {
    private final ShopifySyncScheduler shopifySyncScheduler;
    @PostMapping
    public ResponseEntity<String> sync(){
        shopifySyncScheduler.syncAllTenants();
        return new ResponseEntity<>("sync successful", HttpStatus.OK);
    }
}
