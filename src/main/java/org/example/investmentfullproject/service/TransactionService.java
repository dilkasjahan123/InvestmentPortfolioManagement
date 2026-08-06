package org.example.investmentfullproject.service;

import jakarta.transaction.Transactional;
import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.Transaction;
import org.example.investmentfullproject.model.Transaction.TransactionStatus;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.repository.AssetRepository;
import org.example.investmentfullproject.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AssetRepository assetRepository;
    private final PortfolioService portfolioService;

    public TransactionService(
            TransactionRepository transactionRepository,
            AssetRepository assetRepository,
            PortfolioService portfolioService) {
        this.transactionRepository = transactionRepository;
        this.assetRepository = assetRepository;
        this.portfolioService = portfolioService;
    }

    @Transactional
    public void buyAsset(Transaction transaction, User loggedUser) {
        if (transaction == null || transaction.getAssetId() == null) {
            throw new IllegalArgumentException("Please select an asset.");
        }
        Asset asset = findAccessibleAsset(transaction.getAssetId(),
                loggedUser);
        transaction.setPrice(asset.getCurrentPrice());
        validateTransaction(transaction);

        int existingQuantity = safeQuantity(asset);
        int purchasedQuantity = transaction.getQuantity();
        int updatedQuantity = existingQuantity + purchasedQuantity;

        BigDecimal existingCost = safePrice(asset.getPurchasePrice())
                .multiply(BigDecimal.valueOf(existingQuantity));
        BigDecimal purchaseCost = transaction.getPrice()
                .multiply(BigDecimal.valueOf(purchasedQuantity));
        BigDecimal averagePurchasePrice = existingCost.add(purchaseCost)
                .divide(
                        BigDecimal.valueOf(updatedQuantity),
                        2,
                        RoundingMode.HALF_UP);

        asset.setQuantity(updatedQuantity);
        asset.setPurchasePrice(averagePurchasePrice);
        asset.setActive(true);
        assetRepository.save(asset);

        saveTransaction(transaction, asset, "BUY");
        portfolioService.updatePortfolioValue(
                asset.getPortfolio().getPortfolioId());
    }

    @Transactional
    public void sellAsset(Transaction transaction, User loggedUser) {
        if (transaction == null || transaction.getAssetId() == null) {
            throw new IllegalArgumentException("Please select an asset.");
        }
        Asset asset = findAccessibleAsset(transaction.getAssetId(), loggedUser);
        transaction.setPrice(asset.getCurrentPrice());
        validateTransaction(transaction);

        int existingQuantity = safeQuantity(asset);
        if (transaction.getQuantity() > existingQuantity) {
            throw new IllegalArgumentException(
                    "Sell quantity cannot exceed the available quantity.");
        }

        int remainingQuantity = existingQuantity - transaction.getQuantity();
        asset.setQuantity(remainingQuantity);
        asset.setActive(remainingQuantity > 0);
        assetRepository.save(asset);

        saveTransaction(transaction, asset, "SELL");
        portfolioService.updatePortfolioValue(
                asset.getPortfolio().getPortfolioId());
    }

    public List<Transaction> getAllTransactions(User loggedUser) {
        if (isInvestor(loggedUser)) {
            return transactionRepository
                    .findByAssetPortfolioInvestorUserIdOrderByTransactionDateDesc(
                            loggedUser.getUserId());
        }
        return transactionRepository.findAllByOrderByTransactionDateDesc();
    }

    public List<Transaction> getRecentTransactions(User loggedUser) {
        if (isInvestor(loggedUser)) {
            return transactionRepository
                    .findTop5ByAssetPortfolioInvestorUserIdOrderByTransactionDateDesc(
                            loggedUser.getUserId());
        }
        return transactionRepository.findTop5ByOrderByTransactionDateDesc();
    }
    public long getTransactionCount() {
        return transactionRepository.count();
    }
    public BigDecimal getTotalBuy(User loggedUser) {
        return getTotalByType(loggedUser, "BUY");
    }

    public BigDecimal getTotalSell(User loggedUser) {
        return getTotalByType(loggedUser, "SELL");
    }

    public double getSuccessRate(User loggedUser) {
        long total;
        long success;

        if (isInvestor(loggedUser)) {
            total = transactionRepository
                    .countByAssetPortfolioInvestorUserId(loggedUser.getUserId());
            success = transactionRepository
                    .countByAssetPortfolioInvestorUserIdAndStatus(
                            loggedUser.getUserId(),
                            TransactionStatus.SUCCESS);
        } else {
            total = transactionRepository.count();
            success = transactionRepository.countByStatus(
                    TransactionStatus.SUCCESS);
        }

        return total == 0 ? 0 : (success * 100.0) / total;
    }

    private void saveTransaction(
            Transaction transaction,
            Asset asset,
            String transactionType) {
        transaction.setTransactionId(null);
        transaction.setAsset(asset);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);
    }

    private Asset findAccessibleAsset(Integer assetId, User loggedUser) {
        if (loggedUser == null) {
            throw new IllegalArgumentException(
                    "Please log in to complete a transaction.");
        }

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "The selected asset does not exist."));

        if (!Boolean.TRUE.equals(asset.getActive())) {
            throw new IllegalArgumentException(
                    "The selected asset is not active.");
        }

        if (isInvestor(loggedUser)
                && (asset.getPortfolio() == null
                || asset.getPortfolio().getInvestor() == null
                || !loggedUser.getUserId().equals(
                        asset.getPortfolio().getInvestor().getUserId()))) {
            throw new IllegalArgumentException(
                    "You can transact only with assets in your portfolios.");
        }

        return asset;
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction == null || transaction.getAssetId() == null) {
            throw new IllegalArgumentException("Please select an asset.");
        }
        if (transaction.getQuantity() == null
                || transaction.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }
        if (transaction.getPrice() == null
                || transaction.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero.");
        }
    }

    private BigDecimal getTotalByType(User loggedUser, String type) {
        return getAllTransactions(loggedUser).stream()
                .filter(transaction ->
                        type.equals(transaction.getTransactionType()))
                .map(Transaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isInvestor(User user) {
        return user != null && user.getRole() == Role.INVESTOR;
    }

    private int safeQuantity(Asset asset) {
        return asset.getQuantity() == null ? 0 : asset.getQuantity();
    }

    private BigDecimal safePrice(BigDecimal price) {
        return price == null ? BigDecimal.ZERO : price;
    }
}
