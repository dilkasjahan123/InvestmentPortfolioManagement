const BASE_URL = "http://localhost:8083";

const loggedUser =
    JSON.parse(
        localStorage.getItem(
            "loggedUser"
        )
    );

window.onload = () => {

    loadPortfolios();

    loadAssets();

    if(loggedUser.role === "INVESTOR" ||
        loggedUser.role === "ADVISOR"
    ){

        document.getElementById(
            "addAssetBtn"
        ).style.display = "none";
    }
};
let deleteAssetId = null;
function formatCurrency(amount){
    return amount.toLocaleString("en-IN",{
        style:"currency",
        currency:"INR"
    });
}
async function loadPortfolios(){


    const response =
        await fetch(
            "http://localhost:8083/portfolio/all"
        );

    let portfolios =
        await response.json();

    if(loggedUser.role === "INVESTOR"){

        portfolios =
            portfolios.filter(
                portfolio =>
                    portfolio.investor.userId ===
                    loggedUser.userId
            );
    }

    let options =
        `<option value="">
            Select Portfolio
        </option>`;

    portfolios.forEach(portfolio => {

        options += `

        <option value="${portfolio.portfolioId}">

            ${portfolio.portfolioName}
            (${portfolio.investor.username})

        </option>

        `;
    });

    document.getElementById(
        "portfolioId"
    ).innerHTML = options;

    document.getElementById(
        "updatePortfolioId"
    ).innerHTML = options;
}
function addAsset() {
    const assetName =
        document.getElementById(
            "assetName"
        ).value.trim();

    const portfolioId =
        document.getElementById(
            "portfolioId"
        ).value;

    if(assetName === ""){

        document.getElementById(
            "message"
        ).innerHTML = `
            <div class="alert alert-danger">
                Asset Name is required.
            </div>
        `;

        return;
    }

    if(portfolioId === ""){

        document.getElementById(
            "message"
        ).innerHTML = `
            <div class="alert alert-danger">
                Please select a portfolio.
            </div>
        `;

        return;
    }

    const quantityValue =
        document.getElementById("quantity").value.trim();

    const purchasePriceValue =
        document.getElementById("purchasePrice").value.trim();

    const currentPriceValue =
        document.getElementById("currentPrice").value.trim();

    if (
        quantityValue === "" ||
        purchasePriceValue === "" ||
        currentPriceValue === ""
    ) {

        document.getElementById("message").innerHTML = `
            <div class="alert alert-danger alert-dismissible fade show">
                <strong>Error!</strong> All fields are required.
                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="alert">
                </button>
            </div>
        `;

        return;
    }

    const quantity = parseInt(quantityValue);
    const purchasePrice = parseFloat(purchasePriceValue);
    const currentPrice = parseFloat(currentPriceValue);

    if (quantity <= 0) {

        document.getElementById("message").innerHTML = `
            <div class="alert alert-danger">
                Quantity must be greater than 0.
            </div>
        `;

        return;
    }

    if (purchasePrice <= 0) {

        document.getElementById("message").innerHTML = `
            <div class="alert alert-danger">
                Purchase Price must be greater than 0.
            </div>
        `;

        return;
    }

    if (currentPrice <= 0) {

        document.getElementById("message").innerHTML = `
            <div class="alert alert-danger">
                Current Price must be greater than 0.
            </div>
        `;

        return;
    }


    const asset = {

        assetName:
            document.getElementById(
                "assetName"
            ).value,

        portfolio: {
            portfolioId: parseInt(
                document.getElementById(
                    "portfolioId"
                ).value
            )
        },

        assetType:
            document.getElementById(
                "assetType"
            ).value,

        quantity,
        purchasePrice,
        currentPrice
    };

    fetch(BASE_URL + "/assets/add", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(asset)

    })
    .then(response => response.json())

    .then(() => {

        document.getElementById("message").innerHTML = `
            <div class="alert alert-success alert-dismissible fade show">
                <strong>Success!</strong> Asset Added Successfully.
                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="alert">
                </button>
            </div>
        `;
        document.getElementById("assetName").value = "";
        document.getElementById("portfolioId").value = "";
        document.getElementById("assetType").selectedIndex = 0;
        document.getElementById("quantity").value = "";
        document.getElementById("purchasePrice").value = "";
        document.getElementById("currentPrice").value = "";

        bootstrap.Modal
            .getInstance(
                document.getElementById("addAssetModal")
            )
            .hide();

        loadAssets();

        setTimeout(() => {
            document.getElementById("message").innerHTML = "";
        }, 3000);

    })

    .catch(error => {

        console.error(error);

        document.getElementById("message").innerHTML = `
            <div class="alert alert-danger">
                Failed to Add Asset.
            </div>
        `;

    });

}

function loadAssets() {

        fetch(BASE_URL +"/assets/all")
        .then(response => response.json())

        .then(data => {
            if(loggedUser.role === "INVESTOR"){

                data =
                    data.filter(
                        asset =>
                            asset.portfolio.investor.userId ===
                            loggedUser.userId
                    );
            }
            const searchText =
                document.getElementById(
                    "assetSearch"
                )?.value
                    .toLowerCase()
                    .trim() || "";

            if(searchText){

                data = data.filter(asset =>
                    asset.assetName
                         .toLowerCase()
                         .includes(searchText)
                );
            }
            if(data.length === 0){

                    document.getElementById(
                        "assetContainer"
                    ).innerHTML = `

                    <div class="col-12">

                        <div class="card shadow-sm">

                            <div class="card-body text-center">

                                <h5>
                                    No Assets Found
                                </h5>

                                <p>
                                    Add your first asset.
                                </p>

                            </div>

                        </div>

                    </div>

                    `;

                    return;
                }
            let totalProfit = 0;
            let totalQuantity = 0;

            const grouped = {};
            data.sort((a,b) => {

                if(loggedUser.role === "INVESTOR"){

                    const portfolioCompare =
                        a.portfolio.portfolioName
                        .localeCompare(
                            b.portfolio.portfolioName
                        );

                    if(portfolioCompare !== 0){
                        return portfolioCompare;
                    }

                    return a.assetName
                        .localeCompare(
                            b.assetName
                        );
                }

                const userCompare =
                    a.portfolio.investor.username
                    .localeCompare(
                        b.portfolio.investor.username
                    );

                if(userCompare !== 0){
                    return userCompare;
                }

                const portfolioCompare =
                    a.portfolio.portfolioName
                    .localeCompare(
                        b.portfolio.portfolioName
                    );

                if(portfolioCompare !== 0){
                    return portfolioCompare;
                }

                return a.assetName
                    .localeCompare(
                        b.assetName
                    );
            });

            data.forEach(asset => {

                const key =
                    loggedUser.role === "INVESTOR"
                    ?
                    asset.portfolio.portfolioName
                    :
                    asset.portfolio.investor.username;

                if(!grouped[key]){
                    grouped[key] = [];
                }

                grouped[key].push(asset);

                totalQuantity += asset.quantity;

                totalProfit += (
                    Number(asset.currentPrice)
                    -
                    Number(asset.purchasePrice)
                ) * Number(asset.quantity);

            });


            let html = "";

            Object.keys(grouped)
                 .sort()
                 .forEach(user => {

                     html += createSection(
                         user,
                         grouped[user]
                     );

                 });
            document.getElementById("totalAssets")
                .innerHTML = data.length;

            document.getElementById("totalQuantity")
                .innerHTML = totalQuantity;

            const profitElement =
                document.getElementById("totalProfit");

            profitElement.innerHTML =
                formatCurrency(totalProfit);

            profitElement.className =
                totalProfit >= 0
                    ? "text-white"
                    : "text-warning";
            document.getElementById(
                "assetContainer"
            ).innerHTML = html;

        });

}

function createSection(title, assets){

    if(assets.length === 0){

        return `
            <div class="col-12">

                <h3 class="section-header">
                    ${
                        loggedUser.role === "INVESTOR"
                        ? "📁"
                        : "👤"
                    }
                    ${title}
                </h3>

            </div>

            <div class="col-12 mb-4">

                <div class="card border-0 shadow-sm">

                    <div class="card-body text-center py-4">

                        <i class="bi bi-folder2-open fs-2 text-secondary"></i>

                        <p class="mt-2 mb-0 text-muted">

                            No ${title.replace(/[📈💰📊]/g, "").trim()} Available

                        </p>

                    </div>

                </div>

            </div>
        `;
    }

    let section = `
        <div class="col-12">
            <h3 class="section-header">
                ${
                    loggedUser.role === "INVESTOR"
                    ? "📁"
                    : "👤"
                }
                ${title}
            </h3>
        </div>
    `;

    assets.forEach(asset => {
        const profit =
            (asset.currentPrice - asset.purchasePrice)
            * asset.quantity;

        const profitClass =
            profit >= 0
                ? "text-success"
                : "text-danger";

        const profitLabel =
            profit >= 0
                ? "🟢 Profit"
                : "🔴 Loss";

        section += `

        <div class="col-lg-4 col-md-6 mb-4">

            <div class="card asset-card">

                <div class="card-body">

                    <div class="asset-header">

                        <h5 class="asset-name">
                            ${asset.assetName}
                        </h5>

                        <span class="badge bg-success">
                            ${asset.assetType}
                        </span>

                    </div>




                    <hr>


                    ${
                    loggedUser.role === "ADMIN"
                    ?
                    `
                    <p>
                        <span class="asset-label">
                            Portfolio
                        </span>
                        <br>
                        <strong>
                            ${asset.portfolio.portfolioName}
                        </strong>
                    </p>
                    `
                    :
                    ""
                    }

                    <p>
                        <span class="asset-label">
                            Quantity
                        </span>
                        <br>
                        ${asset.quantity}
                    </p>

                    <p>
                        <span class="asset-label">
                            Purchase Price
                        </span>
                        <br>
                        <span class="asset-value">
                            ${formatCurrency(asset.purchasePrice)}
                        </span>
                    </p>

                    <p>
                        <span class="asset-label">
                            Current Price
                        </span>
                        <br>
                        <span class="asset-value">
                            ${formatCurrency(asset.currentPrice)}
                        </span>
                    </p>

                    <p>
                        <span class="asset-label">
                            Current Value
                        </span>
                        <br>
                        <span class="asset-value">
                            ${formatCurrency(
                                asset.quantity *
                                asset.currentPrice
                            )}
                        </span>
                    </p>

                    <p>
                        <span class="asset-label">
                            ${profitLabel}
                        </span>
                        <br>
                        <span class="${profitClass}">
                            ${formatCurrency(Math.abs(profit))}
                        </span>
                    </p>

                    ${
                    loggedUser.role === "ADMIN"
                    ?
                    `

                    <div class="action-buttons">

                        <button
                            class="btn btn-outline-primary btn-sm me-2"
                            onclick='showUpdate(
                                ${asset.assetId},
                                "${asset.assetName}",
                                ${asset.portfolio.portfolioId},
                                "${asset.assetType}",
                                ${asset.quantity},
                                ${asset.purchasePrice},
                                ${asset.currentPrice}
                            )'>

                            <i class="bi bi-pencil"></i>

                            Edit

                        </button>

                        <button
                            class="btn btn-outline-danger btn-sm"
                            onclick='deleteAsset(${asset.assetId})'>

                            <i class="bi bi-trash"></i>

                            Delete

                        </button>

                    </div>

                    `
                    :
                    ``

                    }
                </div>

            </div>

        </div>

        `;

    });

    return section;
}

function showUpdate(id,name,portfolioId,type,qty,purchase,current){
    document.getElementById("updateAssetId").value=id;
    document.getElementById("updateAssetName").value = name;

    document.getElementById("updatePortfolioId").value = portfolioId;
    document.getElementById("updateType").value=type;
    document.getElementById("updateQuantity").value=qty;
    document.getElementById("updatePurchasePrice").value=purchase;
    document.getElementById("updateCurrentPrice").value=current;

    new bootstrap.Modal(
        document.getElementById("updateModal")
    ).show();
}

function updateAsset() {

    const asset = {

        assetId:
            document.getElementById(
                "updateAssetId"
            ).value,

        assetName:
            document.getElementById(
                "updateAssetName"
            ).value,

        portfolio:{
            portfolioId: parseInt(
                document.getElementById(
                    "updatePortfolioId"
                ).value
            )
        },

        assetType:
            document.getElementById(
                "updateType"
            ).value,



        purchasePrice: parseFloat(
            document.getElementById(
                "updatePurchasePrice"
            ).value
        ),

        currentPrice: parseFloat(
            document.getElementById(
                "updateCurrentPrice"
            ).value
        )
    };

    fetch(BASE_URL + "/assets/update", {

        method: "PUT",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(asset)

    })

    .then(response => response.json())

    .then(() => {

        // Success Message
        document.getElementById("message").innerHTML = `
            <div class="alert alert-success alert-dismissible fade show">
                <strong>Success!</strong> Asset Updated Successfully.
                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="alert">
                </button>
            </div>
        `;

        // Close Modal
        bootstrap.Modal
            .getInstance(
                document.getElementById("updateModal")
            )
            .hide();

        // Refresh Assets
        loadAssets();

        // Auto Hide Message
        setTimeout(() => {
            document.getElementById("message").innerHTML = "";
        }, 3000);

    })

    .catch(error => {

        console.error(error);

        document.getElementById("message").innerHTML = `
            <div class="alert alert-danger">
                Asset Update Failed
            </div>
        `;
    });
}

function deleteAsset(assetId){

    deleteAssetId = assetId;

    new bootstrap.Modal(
        document.getElementById("deleteModal")
    ).show();

}
document.addEventListener("DOMContentLoaded", () => {

    document
        .getElementById("confirmDeleteBtn")
        .addEventListener("click", () => {

            fetch(
                BASE_URL + "/assets/" + deleteAssetId,
                {
                    method: "DELETE"
                }
            )

            .then(() => {

                document.getElementById("message").innerHTML = `
                    <div class="alert alert-success alert-dismissible fade show">
                        <strong>Success!</strong>
                        Asset Deleted Successfully.
                        <button type="button"
                                class="btn-close"
                                data-bs-dismiss="alert">
                        </button>
                    </div>
                `;

                bootstrap.Modal
                    .getInstance(
                        document.getElementById("deleteModal")
                    )
                    .hide();

                loadAssets();

                setTimeout(() => {
                    document.getElementById("message").innerHTML = "";
                }, 3000);

            });

        });

});
