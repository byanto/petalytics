package com.budiyanto.petalytics.petalyticsbackend.controller;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.service.OrderIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class OrderIngestionController {

    private final OrderIngestionService orderIngestionService;

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file, @RequestParam("marketplace") String marketplace) throws IOException {
        orderIngestionService.ingest(file.getInputStream(), Marketplace.valueOf(marketplace.toUpperCase()));
        return ResponseEntity.ok().build();
    }

}