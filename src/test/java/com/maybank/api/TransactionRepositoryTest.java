package com.maybank.api;

import com.maybank.api.controller.TransactionController;
import com.maybank.api.model.Transaction;
import com.maybank.api.repository.TransactionRepository;
import com.maybank.api.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void searchTransactions_ReturnsMatchTransaction() {
        Transaction testTransaction1 = Transaction.builder()
                .accountNumber("12345")
                .customerId("123451")
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

        Transaction testTransaction3 = Transaction.builder()
                .accountNumber("75395")
                .customerId("753951")
                .trxAmount(new BigDecimal("12.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 30))
                .description("Test Transaction")
                .build();

        transactionRepository.save(testTransaction1);
        transactionRepository.save(testTransaction2);
        transactionRepository.save(testTransaction3);

        List<String> accountNumbers = List.of("12345", "67890");

        Page<Transaction> transactionList = transactionRepository.searchTransactions(null, accountNumbers, "BILL PAYMENT", null);
        Assertions.assertNotNull(transactionList);
        Assertions.assertEquals(transactionList.getSize(), 2);

    }
}
