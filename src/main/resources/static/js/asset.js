document.addEventListener("DOMContentLoaded", () => {
    initializeAssetSearch();
    initializeValuePreview();
});

function initializeAssetSearch() {
    const searchInput = document.getElementById("assetSearch");

    if (!searchInput) {
        return;
    }

    searchInput.addEventListener("input", () => {
        const searchTerm = searchInput.value.trim().toLowerCase();

        document.querySelectorAll(".portfolio-group").forEach((group) => {
            const groupText =
                    group.querySelector(".portfolio-group-header")
                        ?.textContent.toLowerCase() ?? "";

            const groupMatches = groupText.includes(searchTerm);
            let hasVisibleAsset = false;

            group.querySelectorAll(".asset-card").forEach((card) => {
                const cardText = card.textContent.toLowerCase();
                const matches =
                        groupMatches || cardText.includes(searchTerm);

                card.hidden = !matches;
                hasVisibleAsset = hasVisibleAsset || matches;
            });

            group.hidden = !hasVisibleAsset;
        });
    });
}

function initializeValuePreview() {
    const quantityInput = document.getElementById("quantity");
    const purchasePriceInput = document.getElementById("purchasePrice");
    const currentPriceInput = document.getElementById("currentPrice");

    if (!quantityInput || !purchasePriceInput || !currentPriceInput) {
        return;
    }

    const updatePreview = () => {
        const quantity = Number(quantityInput.value) || 0;
        const purchasePrice = Number(purchasePriceInput.value) || 0;
        const currentPrice = Number(currentPriceInput.value) || 0;

        const investedValue = quantity * purchasePrice;
        const currentValue = quantity * currentPrice;
        const profitLoss = currentValue - investedValue;
        const returnPercentage =
                investedValue > 0
                        ? (profitLoss / investedValue) * 100
                        : 0;

        setPreviewText("previewInvestedValue", formatCurrency(investedValue));
        setPreviewText("previewCurrentValue", formatCurrency(currentValue));
        setPreviewText(
                "previewProfitLoss",
                `${profitLoss >= 0 ? "+" : "-"}${formatCurrency(
                    Math.abs(profitLoss)
                )}`
        );
        setPreviewText(
                "previewReturn",
                `${returnPercentage >= 0 ? "+" : ""}${returnPercentage.toFixed(2)}%`
        );

        updatePerformanceClass("previewProfitLoss", profitLoss);
        updatePerformanceClass("previewReturn", returnPercentage);
    };

    [quantityInput, purchasePriceInput, currentPriceInput]
        .forEach((input) => input.addEventListener("input", updatePreview));

    updatePreview();
}

function setPreviewText(elementId, value) {
    const element = document.getElementById(elementId);

    if (element) {
        element.textContent = value;
    }
}

function updatePerformanceClass(elementId, value) {
    const element = document.getElementById(elementId);

    if (!element) {
        return;
    }

    element.classList.remove("positive-value", "negative-value");
    element.classList.add(
        value >= 0 ? "positive-value" : "negative-value"
    );
}

function formatCurrency(value) {
    return new Int.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(value);
}
