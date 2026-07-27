package org.example.investmentfullproject.repository;

import org.example.investmentfullproject.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset,Integer> {

    List<Asset> findByPortfolioPortfolioId(Integer portfolioId);
    List<Asset> findByActiveTrue();

}
