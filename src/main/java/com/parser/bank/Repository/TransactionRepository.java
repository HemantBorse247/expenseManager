package com.parser.bank.Repository;



//import com.parser.bank.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parser.bank.Entity.Transaction;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    List<Transaction> findByTransactionDateBetweenAndInclude(LocalDate startDate, LocalDate endDate, Boolean include);
}
