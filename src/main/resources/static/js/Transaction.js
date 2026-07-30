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

function validateBuy() {
    return validateTransactionForm();
}

function validateSellForm() {
    return validateTransactionForm();
}

