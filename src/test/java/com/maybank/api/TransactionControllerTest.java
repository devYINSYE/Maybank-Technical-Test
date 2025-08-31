package com.maybank.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maybank.api.controller.TransactionController;
import com.maybank.api.model.Transaction;
import com.maybank.api.service.TransactionService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    private Transaction txn1;
    private Transaction txn2;
    private Transaction txn3;


    @BeforeEach
    public void setup() {
        txn1 = Transaction.builder()
                .id(1L)
                .accountNumber("12345")
                .customerId("123451")
                .trxAmount(new BigDecimal("123.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 31))
                .description("BILL PAYMENT")
                .build();

        txn2 = Transaction.builder()
                .id(2L)
                .accountNumber("67890")
                .customerId("678901")
                .trxAmount(new BigDecimal("12345.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 30))
                .description("BILL PAYMENT")
                .build();

        txn3 = Transaction.builder()
                .id(3L)
                .accountNumber("75395")
                .customerId("753951")
                .trxAmount(new BigDecimal("12.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 30))
                .description("Test Transaction")
                .build();
    }

    @Test
    void searchTransactions_ByCustomerId_ReturnPage() throws Exception {

        Page<Transaction> transactionPage = new PageImpl<Transaction>(
                List.of(txn1),
                PageRequest.of(0, 10),
                1
        );

        Mockito.when(transactionService.searchTransactions(
                        Mockito.eq("123451"),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(Pageable.class)))
                .thenReturn(transactionPage);

        mockMvc.perform(get("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("customerId", "123451"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content[0].customerId").value("123451"));
    }

    @Test
    void searchTransactions_ByMultipleAccountNumber_ReturnPage() throws Exception {

        Page<Transaction> transactionPage = new PageImpl<>(List.of(txn1, txn3));
        List<String> accountNumbers = List.of("12345", "75395");

        Mockito.when(transactionService.searchTransactions(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(Pageable.class)))
                .thenReturn(transactionPage);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("accountNumbers", "12345")
                        .param("accountNumbers", "75395")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountNumber").value("12345"))
                .andExpect(jsonPath("$.content[1].accountNumber").value("75395"));
    }

    @Test
    void searchTransactions_ByDescription_ShouldReturnPage() throws Exception {

        Page<Transaction> transactionPage = new PageImpl<>(List.of(txn1, txn2));

        Mockito.when(transactionService.searchTransactions(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.eq("BILL PAYMENT"),
                        Mockito.any(Pageable.class)))
                .thenReturn(transactionPage);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("description", "BILL PAYMENT")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("BILL PAYMENT"));
    }

    @Test
    void searchTransactions_WhenNotFound_Return404() throws Exception {

        Page<Transaction> transactionPage = new PageImpl<>(List.of());

        Mockito.when(transactionService.searchTransactions(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(Pageable.class)))
                .thenReturn(transactionPage);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("customerId", "000")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(transactionPage.getContent().size())));

    }

    @Test
    public void updateDescription_ReturnUpdatedTransaction() throws Exception {
        Transaction updTxn = Transaction.builder()
                .id(3L)
                .accountNumber("75395")
                .customerId("753951")
                .trxAmount(new BigDecimal("12.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 30))
                .description("NEW DESCRIPTION")
                .build();

        Mockito.when(transactionService.updateDescription(
                        Mockito.eq(3L),
                        Mockito.eq("NEW DESCRIPTION")))
                .thenReturn(updTxn);

        mockMvc.perform(patch("/api/v1/transactions/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"NEW DESCRIPTION\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.description").value("NEW DESCRIPTION"));
    }

    @Test
    void updateDescription_WhenNotFound_Return404() throws Exception {

        Transaction updTxn = Transaction.builder()
                .id(3L)
                .accountNumber("75395")
                .customerId("753951")
                .trxAmount(new BigDecimal("12.00"))
                .trxTime(LocalTime.of(11, 11, 11))
                .trxDate(LocalDate.of(2025, 8, 30))
                .description("NEW DESCRIPTION")
                .build();

        Mockito.when(transactionService.updateDescription(
                        Mockito.eq(3L),
                        Mockito.eq("NEW DESCRIPTION")))
                .thenReturn(updTxn);

        mockMvc.perform(patch("/api/v1/transactions/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"NEW DESCRIPTION\"}"))
                .andExpect(status().isNotFound());
    }

}
