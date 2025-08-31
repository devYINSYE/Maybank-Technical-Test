package com.maybank.api;

import com.maybank.api.model.Transaction;
import com.maybank.api.repository.TransactionRepository;
import com.maybank.api.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void searchTransactions_ReturnsMatchTransaction() {
        Transaction testTransaction1 = Transaction.builder()
                .accountNumber("67890")
                .customerId("678901")
                .trxAmount(new BigDecimal("123.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 31))
                .description("BILL PAYMENT")
                .build();

        Transaction testTransaction2 = Transaction.builder()
                .accountNumber("67890")
                .customerId("678901")
                .trxAmount(new BigDecimal("12345.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 30))
                .description("BILL PAYMENT")
                .build();

        List<Transaction> transactions = List.of(testTransaction1, testTransaction2);
        Page<Transaction> transactionPage = new PageImpl<>(transactions);

        // Set account numbers list
        List<String> accountNumbers = List.of("75395");

        // Mock repository method
        Mockito.when(transactionRepository.searchTransactions(
                        Mockito.any(),
                        Mockito.anyList(),
                        Mockito.any(),
                        Mockito.any(Pageable.class)))
                .thenReturn(transactionPage);

        // Call service
        Page<Transaction> result = transactionService.searchTransactions(null, accountNumbers, null, PageRequest.of(0, 1));

        // Assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTotalElements());
    }

    @Test
    public void updateDescription_ReturnUpdatedTransaction() {

        Transaction oriTransaction = Transaction.builder()
                .accountNumber("67890")
                .customerId("67890")
                .trxAmount(new BigDecimal("123.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 31))
                .description("BILL PAYMENT")
                .build();

        Transaction updTransaction = Transaction.builder()
                .accountNumber("67890")
                .customerId("67890")
                .trxAmount(new BigDecimal("123.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 31))
                .description("Updated Description")
                .build();

        // Mock repository method
        Mockito.when(transactionRepository.findById(1L)).thenReturn(Optional.of(oriTransaction));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(updTransaction);

        // Call service
       Transaction result = transactionService.updateDescription(1L,"Updated Description");

        // Assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals(updTransaction.getDescription(), result.getDescription());
    }
}
