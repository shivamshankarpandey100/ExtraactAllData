package com.extract.ExtraactAllData.controller;

import com.extract.ExtraactAllData.model.ExtractedData;
import com.extract.ExtraactAllData.service.DataExtractionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data-extraction")
@CrossOrigin(origins = "*")
public class DataExtractionController {

    @Autowired
    private DataExtractionService dataExtractionService;

    @PostMapping("/extract")
    public ResponseEntity<ApiResponse> extractData(@RequestBody String inputText) {
        if (inputText == null || inputText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("FAILED", null));
        }

        List<ExtractedData> extractedData = dataExtractionService.extractDataFromText(inputText);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", extractedData));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Data Extraction Service is running");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ApiResponse {
        private String status;
        private List<ExtractedData> data;
    }
}
