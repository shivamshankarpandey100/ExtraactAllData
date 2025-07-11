package com.extract.ExtraactAllData.controller;

import com.extract.ExtraactAllData.excelGenrator.ExcelGenerator;
import com.extract.ExtraactAllData.model.ExtractedData;
import com.extract.ExtraactAllData.service.DataExtractionService;
import com.extract.ExtraactAllData.service.TextService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Autowired
    private TextService textService;

    @PostMapping("/format")
    public String formatText(@RequestBody String rawText) {
        return textService.formatInput(rawText);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Data Extraction Service is running");
    }
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateExcel(@RequestBody List<ExtractedData> dataList) {
        try {
            byte[] excelBytes = ExcelGenerator.convertToExcel(dataList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "extracted_data.xlsx");
            headers.setContentLength(excelBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(("Error generating Excel: " + e.getMessage()).getBytes());
        }
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ApiResponse {
        private String status;
        private List<ExtractedData> data;
    }
}
