document.addEventListener("DOMContentLoaded", () => {
    const assetSelect = document.getElementById("assetId");
    const priceInput = document.getElementById("price");

    if (!assetSelect || !priceInput) {
        return;
    }

    // Automatically display selected asset price
    const updatePrice = () => {
        const selectedOption =
            assetSelect.options[assetSelect.selectedIndex];
        priceInput.value = selectedOption?.dataset.price || "";
    };

    assetSelect.addEventListener("change", updatePrice);
    updatePrice();
});

// Validate common transaction inputs
function validateTransactionForm() {
    const assetId = document.getElementById("assetId").value;
    const quantity = Number(document.getElementById("quantity").value);
    const price = Number(document.getElementById("price").value);

    if (!assetId) {
        alert("Please select an asset.");
        return false;
    }

    if (!Number.isInteger(quantity) || quantity <= 0) {
        alert("Quantity must be a whole number greater than zero.");
        return false;
    }

    if (!Number.isFinite(price) || price <= 0) {
        alert("Price must be greater than zero.");
        return false;
    }

    return true;
}

// Validate buy transaction
function validateBuy() {
    return validateTransactionForm();
}

// Validate sell transaction
function validateSellForm() {
    return validateTransactionForm();
}

