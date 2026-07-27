package org.example.investmentfullproject.controller;

import org.example.investmentfullproject.model.Asset;
import org.example.investmentfullproject.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @PostMapping("/add")
    public Asset addAsset(@RequestBody Asset asset) {
        return assetService.addAsset(asset);
    }

    @PutMapping("/update")
    public Asset updateAsset(@RequestBody Asset asset) {
        return assetService.updateAsset(asset);
    }

    @GetMapping("/portfolio/{portfolioId}")
    public List<Asset> getAssetsByPortfolio(
            @PathVariable Integer portfolioId) {

        return assetService.getAssetsByPortfolio(portfolioId);
    }

    @DeleteMapping("/{assetId}")
    public String deleteAsset(
            @PathVariable Integer assetId) {

        assetService.deleteAsset(assetId);

        return "Asset Deleted Successfully";
    }
    @GetMapping("/all")
    public List<Asset> getAllAssets(){

        return assetService.getAllAssets();
    }
    @GetMapping("/count")
    public long getAssetCount(){

        return assetService.getAssetCount();
    }
}
