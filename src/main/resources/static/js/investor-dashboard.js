const loggedUser =
    JSON.parse(
        localStorage.getItem(
            "loggedUser"
        )
    );

const PORTFOLIO_API =
    "http://localhost:8083/portfolio/all";

const ASSET_API =
    "http://localhost:8083/assets/all";

loadDashboard();

async function loadDashboard(){

    document.getElementById(
        "investorName"
    ).textContent =
        loggedUser.username;

    const portfolios =
        await fetch(PORTFOLIO_API)
            .then(r => r.json());

    const assets =
        await fetch(ASSET_API)
            .then(r => r.json());

    const myPortfolios =
        portfolios.filter(
            p =>
                p.investor.userId ==
                loggedUser.userId
        );

    let assetCount = 0;
    let invested = 0;
    let current = 0;

    myPortfolios.forEach(portfolio => {

        const portfolioAssets =
            assets.filter(
                a =>
                    a.portfolio.portfolioId ==
                    portfolio.portfolioId
            );

        assetCount +=
            portfolioAssets.length;

        portfolioAssets.forEach(asset => {

            invested +=
                asset.purchasePrice *
                asset.quantity;

            current +=
                asset.currentPrice *
                asset.quantity;
        });

    });

    document.getElementById(
        "portfolioCount"
    ).textContent =
        myPortfolios.length;

    document.getElementById(
        "assetCount"
    ).textContent =
        assetCount;

    document.getElementById(
        "investmentValue"
    ).textContent =
        "₹" +
        invested.toFixed(2);

    document.getElementById(
        "currentValue"
    ).textContent =
        "₹" +
        current.toFixed(2);

    renderPortfolios(
        myPortfolios
    );
}

function renderPortfolios(
    portfolios
){

    const container =
        document.getElementById(
            "portfolioContainer"
        );

    container.innerHTML = "";

    portfolios.forEach(
        portfolio => {

            container.innerHTML += `

            <div class="portfolio-card">

                <h3>
                    ${portfolio.portfolioName}
                </h3>

                <p>
                    Risk Level :
                    ${portfolio.riskLevel || "N/A"}
                </p>

                <p>
                    Current Value :
                    ₹${portfolio.totalValue}
                </p>

                <button
                    onclick="goToAnalytics()">

                    View Analytics

                </button>

            </div>

            `;
        }
    );
}

function goToPortfolio(){

    window.location.href =
        "portfolio.html";
}

function goToAssets(){

    window.location.href =
        "asset.html";
}

function goToTransactions(){

    window.location.href =
        "transaction.html";
}

function goToAnalytics(){

    window.location.href =
        "investor-analytics.html";
}

function goToProfile(){

    window.location.href =
        "profile.html";
}

function logout(){

    localStorage.removeItem(
        "loggedUser"
    );

    window.location.href =
        "login.html";
}