package com.budiyanto.petalytics.petalyticsbackend.ordering.infrastructure.adapter.in.web;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.in.IngestOrderUseCase;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class OrderIngestionController {

    private final IngestOrderUseCase ingestOrderUseCase;

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file, @RequestParam("marketplace") String marketplace) throws IOException {
        ingestOrderUseCase.ingest(file.getInputStream(), Marketplace.valueOf(marketplace.toUpperCase()));
        return ResponseEntity.ok().build();
    }

}