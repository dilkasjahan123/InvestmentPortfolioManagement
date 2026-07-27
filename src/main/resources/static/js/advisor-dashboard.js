const USER_API =
    "http://localhost:8083/auth/users";

const PORTFOLIO_API =
    "http://localhost:8083/portfolio/all";

const ASSET_API =
    "http://localhost:8083/assets/all";

loadDashboard();
const loggedUser =
    JSON.parse(
        localStorage.getItem(
            "loggedUser"
        )
    );
document.addEventListener(
    "DOMContentLoaded",
    () => {

        document.getElementById(
            "welcomeMessage"
        ).textContent =
            `Welcome, ${loggedUser.username}`;

    }
);

async function loadDashboard(){

    const users =
        await fetch(USER_API)
            .then(r => r.json());

    const portfolios =
        await fetch(PORTFOLIO_API)
            .then(r => r.json());

    const assets =
        await fetch(ASSET_API)
            .then(r => r.json());

    const investors =
        users.filter(
            user =>
                user.role === "INVESTOR"
        );

    document.getElementById(
        "investorCount"
    ).textContent =
        investors.length;

    document.getElementById(
        "portfolioCount"
    ).textContent =
        portfolios.length;

    document.getElementById(
        "assetCount"
    ).textContent =
        assets.length;

    let totalValue = 0;

    portfolios.forEach(portfolio => {

        totalValue +=
            Number(portfolio.totalValue);
    });

    document.getElementById(
        "totalValue"
    ).textContent =
        "₹" +
        totalValue.toLocaleString(
            "en-IN"
        );

    renderInvestorSummary(
        investors,
        portfolios
    );
}

function renderInvestorSummary(
    investors,
    portfolios
){

    const container =
        document.getElementById(
            "investorContainer"
        );

    container.innerHTML = "";

    investors.forEach(investor => {

        const investorPortfolios =
            portfolios.filter(
                p =>
                    p.investor &&
                    p.investor.userId ===
                    investor.userId
            );

        let totalValue = 0;

        investorPortfolios.forEach(
            portfolio => {

            totalValue +=
                Number(
                    portfolio.totalValue
                );
        });

        container.innerHTML += `

        <div class="investor-card">

            <h3>
                ${investor.username}
            </h3>

            <p>
                Email:
                ${investor.email}
            </p>

            <p>
                Portfolios:
                ${investorPortfolios.length}
            </p>

            <p>
                Total Value:
                ₹${totalValue.toLocaleString("en-IN")}
            </p>

        </div>

        `;
    });
}

function goToPortfolio(){

    window.location.href =
        "portfolio.html";
}

function goToAssets(){

    window.location.href =
        "asset.html";
}

function goToPerformanceReport(){

    window.location.href =
        "performance-report.html";
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