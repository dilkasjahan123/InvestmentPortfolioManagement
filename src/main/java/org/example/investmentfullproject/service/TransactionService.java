package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.*;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private PortfolioService portfolioService;

    public Transaction createTransaction(
            Transaction transaction){

        Asset asset =
                assetRepository.findById(
                                transaction.getAsset()
                                        .getAssetId()
                        )
                        .orElse(null);

        if(transaction.getTransactionType()
                == TransactionType.BUY){

            if(asset == null){

                throw new RuntimeException(
                        "Asset does not exist. Admin must create asset."
                );
            }

            asset.setQuantity(
                    asset.getQuantity()
                            +
                            transaction.getQuantity()
            );
        }

        else{

            if(asset == null){

                throw new RuntimeException(
                        "Asset Not Found"
                );
            }

            if(transaction.getQuantity()
                    > asset.getQuantity()){

                throw new RuntimeException(
                        "Insufficient Quantity"
                );
            }

            asset.setQuantity(
                    asset.getQuantity()
                            -
                            transaction.getQuantity()
            );

            if(asset.getQuantity() == 0){

                asset.setActive(false);
            }
        }

        assetRepository.save(asset);

        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        transaction.setTransactionDate(
                LocalDateTime.now()
        );

        Transaction saved =
                transactionRepository.save(
                        transaction
                );

        portfolioService.updatePortfolioValue(
                asset.getPortfolio()
                        .getPortfolioId()
        );

        return saved;
    }
    public long getTransactionCount(){

        return transactionRepository.count();
    }
}