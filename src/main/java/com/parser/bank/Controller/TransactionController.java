package com.parser.bank.Controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import com.parser.bank.DTO.CustomSummaryRequest;
import com.parser.bank.Entity.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.parser.bank.DTO.MonthlySummaryResponse;
import com.parser.bank.DTO.UploadResponse;
import com.parser.bank.Entity.Transaction;
import com.parser.bank.Service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Ensure CORS is enabled for Angular
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UploadResponse("Please select a file to upload.", 0));
        }
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UploadResponse("Only .xlsx files are allowed.", 0));
        }
        try {
            List<Transaction> savedTransactions = transactionService.processExcelFile(file);
            // Return JSON response
            UploadResponse response = new UploadResponse("File uploaded and processed successfully.",
                    savedTransactions.size());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // Return JSON response for error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UploadResponse("Failed to upload and process file: " + e.getMessage(), 0));
        }
    }

    @GetMapping("/monthly-spend")
    public ResponseEntity<Map<String, Double>> getMonthlySpend() {
        Map<String, Double> monthlySpend = transactionService.calculateMonthlySpend();
        return ResponseEntity.ok(monthlySpend);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        try {
            List<Transaction> transactionsList = transactionService.getAllTransactions();
            return ResponseEntity.ok(transactionsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<Transaction>());
        }
    }

    // New endpoint for monthly financial summary
//    @GetMapping("/summary/{year}/{month}")
//    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(@PathVariable int year, @PathVariable int month) {
//        try {
//            MonthlySummaryResponse summary = transactionService.getMonthlyFinancialSummary(year, month);
//            return ResponseEntity.ok(summary);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new MonthlySummaryResponse()); // Return an error map
//        }
//    }

    @PostMapping("/customSummary")
    public ResponseEntity<MonthlySummaryResponse> getCustomSummary(@RequestBody CustomSummaryRequest dateObject) {
        Date startDate = dateObject.getStartdate();
        Date endDate = dateObject.getEnddate();
        try {
            MonthlySummaryResponse summary = transactionService.getCustomPeriodSummary(startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MonthlySummaryResponse()); // Return an error map
        }
    }

    @GetMapping("/summary/{year}")
    public ResponseEntity<Map<String, Double>> getYearlySummary(@PathVariable int year) {
        try {
            Map<String, Double> summary = transactionService.getYearlyFinancialSummary(year);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", -1.0)); // Return an error map
        }
    }

    @PutMapping("/setCategory")
    public ResponseEntity<UploadResponse> setCategory() {
        try {
            return transactionService.setCategoryForTransactions();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UploadResponse(e.getMessage(), 0));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateInclude(@PathVariable long id, @RequestBody Transaction t) {
        try {
            return transactionService.updateInclude(id, t);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Transaction());
        }
    }

    @GetMapping("/category")
    public ResponseEntity<List<Category>> getCategory() {
        try {
            return transactionService.getCategory();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
        }
    }
}