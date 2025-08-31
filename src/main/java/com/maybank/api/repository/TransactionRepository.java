package com.maybank.api.repository;

import com.maybank.api.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t " +
            "WHERE (:customerId IS NULL OR t.customerId = :customerId) " +
            "AND (:accountNumber IS NULL OR t.accountNumber IN :accountNumber) " +
            "AND (:description IS NULL OR t.description = :description)")
    Page<Transaction> searchTransactions(
            @Param("customerId") String customerId,
            @Param("accountNumber") List<String> accountNumber,
            @Param("description") String description,
            Pageable pageable
    );

}
