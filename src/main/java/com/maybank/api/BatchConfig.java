package com.maybank.api;

import com.maybank.api.model.Transaction;
import com.maybank.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final TransactionRepository transactionRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Value("${batch.file.path}")
    private Resource dataSource;

    @Bean
    public FlatFileItemReader<Transaction> reader() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .resource(dataSource)
                .delimited()
                .delimiter("|")
                .names("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId")
                .fieldSetMapper(new TransactionFieldSetMapper())
                .build();
    }

    @Bean
    public ItemWriter<Transaction> writer() {
        return items -> transactionRepository.saveAll(items);
    }

    @Bean
    public Step importStep() {
        return new StepBuilder("importStep", jobRepository)
                .<Transaction, Transaction>chunk(15, transactionManager)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importJob() {
        return new JobBuilder("importJob", jobRepository)
                .start(importStep())
                .build();
    }

}
