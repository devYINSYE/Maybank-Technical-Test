package com.maybank.api.controller;

import com.maybank.api.model.Transaction;
import com.maybank.api.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // http://localhost:8081/api/v1/transactions?accountNumber=8872838283&accountNumber=6872838260&size=1&description=3rd Party FUND TRANSFER
    // API to search by customer ID or account number(s) or description
    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(@RequestParam(required = false) String customerId,
                                                             @RequestParam(required = false) List<String> accountNumber,
                                                             @RequestParam(required = false) String description,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Transaction> results = transactionService.searchTransactions(customerId, accountNumber, description, pageable);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(results);
    }

    // http://localhost:8081/api/v1/transactions/1
    // API to update description
    @PatchMapping("/{id}")
    public ResponseEntity<Transaction> updateDescription(@PathVariable long id,
                                                         @RequestBody Map<String, String> body) {
        String description = body.get("description");
        Transaction updated = transactionService.updateDescription(id, description);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updated);
    }

}
