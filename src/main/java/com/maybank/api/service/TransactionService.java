package com.maybank.api.service;

import com.maybank.api.model.Transaction;
import com.maybank.api.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public Page<Transaction> searchTransactions(String customerId, List<String> accountNumber, String description, Pageable pageable) {
        return transactionRepository.searchTransactions(customerId,accountNumber, description, pageable);
    }

    @Transactional
    public Transaction updateDescription(Long id, String description) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }


}
