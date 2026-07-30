package org.example.investmentfullproject.repository;

import org.example.investmentfullproject.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Integer> {

    List<Transaction> findAllByOrderByTransactionDateDesc();

    List<Transaction> findTop5ByOrderByTransactionDateDesc();

    List<Transaction> findByAssetPortfolioInvestorUserIdOrderByTransactionDateDesc(
            Integer userId);

    List<Transaction>
    findTop5ByAssetPortfolioInvestorUserIdOrderByTransactionDateDesc(
            Integer userId);

    long countByStatus(Transaction.TransactionStatus status);

    long countByAssetPortfolioInvestorUserId(Integer userId);

    long countByAssetPortfolioInvestorUserIdAndStatus(
            Integer userId,
            Transaction.TransactionStatus status);
}
