package org.example.investmentfullproject.controller;

import org.example.investmentfullproject.model.Portfolio;
import org.example.investmentfullproject.repository.PortfolioRepository;
import org.example.investmentfullproject.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @PostMapping("/add")
    public Portfolio createPortfolio(
            @RequestBody Portfolio portfolio) {

        return portfolioService.createPortfolio(portfolio);
    }

    @PutMapping("/update")
    public Portfolio updatePortfolio(
            @RequestBody Portfolio portfolio) {

        return portfolioService.updatePortfolio(portfolio);
    }

    @GetMapping("/{portfolioId}")
    public Portfolio getPortfolioById(
            @PathVariable Integer portfolioId) {

        return portfolioService.getPortfolioById(portfolioId);
    }

    @GetMapping("/all")
    public List<Portfolio> getAllPortfolios() {

        return portfolioService.getAllPortfolios();
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<String> deletePortfolio(
            @PathVariable Integer portfolioId) {

        try {

            portfolioService.deletePortfolio(portfolioId);

            return ResponseEntity.ok(
                    "Portfolio Deleted Successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @GetMapping("/count")
    public Long getPortfolioCount() {

        return portfolioRepository.count();
    }
}