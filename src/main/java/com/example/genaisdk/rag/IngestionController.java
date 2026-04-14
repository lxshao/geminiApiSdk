package com.example.genaisdk.rag;

import com.example.genaisdk.service.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class IngestionController {

    private static final Logger log = LoggerFactory.getLogger(IngestionController.class);

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/api/v1/files/ingest")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
            @RequestParam("ingestType") String ingestType) {
        log.info("Received file: {}, ingestType: {}", file.getOriginalFilename(), ingestType);
        try {
            byte[] fileContent = file.getBytes();

            ingestionService.ingest(fileContent, file.getOriginalFilename(), ingestType);
            return ResponseEntity.ok("File uploaded and process successfully");
        } catch (Exception e) {
            log.error("Error processing file upload", e);
            return ResponseEntity.status(500).body("Failed to process file upload");
        }


    }

}
