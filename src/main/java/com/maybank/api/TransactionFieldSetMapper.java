package com.maybank.api;

import com.maybank.api.model.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(fieldSet.readString("accountNumber"));
        transaction.setTrxAmount(fieldSet.readBigDecimal("trxAmount"));
        transaction.setDescription(fieldSet.readString("description"));

        // Convert date + time
        String dateStr = fieldSet.readString("trxDate");
        String timeStr = fieldSet.readString("trxTime");

        if (dateStr != null && !dateStr.isBlank()) {
            transaction.setTrxDate(LocalDate.parse(dateStr, dateFormatter));
        }
        if (timeStr != null && !timeStr.isBlank()) {
            transaction.setTrxTime(LocalTime.parse(timeStr, timeFormatter));
        }

        transaction.setCustomerId(fieldSet.readString("customerId"));
        return transaction;
    }
}
