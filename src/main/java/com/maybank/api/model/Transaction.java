package com.maybank.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_number", nullable = false)
    private String accountNumber;
    @Column(name = "trx_amount", nullable = false)
    private BigDecimal trxAmount;
    private String description;
    @Column(name = "trx_date", nullable = false)
    private LocalDate trxDate;
    @Column(name = "trx_time", nullable = false)
    private LocalTime trxTime;
    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Version
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
