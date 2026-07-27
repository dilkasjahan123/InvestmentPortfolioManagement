package org.example.investmentfullproject.repository;

import org.example.investmentfullproject.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction,Integer> {

    List<Transaction> findByAssetAssetId(
            Integer assetId
    );
}
