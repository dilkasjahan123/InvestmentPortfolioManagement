package org.example.investmentfullproject.model;

public enum AssetType {

    Stock("Stock"),
    Bond("Bond"),
    Mutual_Fund("Mutual Fund");

    private final String displayName;

    AssetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
