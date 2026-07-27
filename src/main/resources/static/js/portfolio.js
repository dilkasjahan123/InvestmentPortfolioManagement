const API_URL =
    "http://localhost:8083/portfolio";

const loggedUser =
    JSON.parse(
        localStorage.getItem(
            "loggedUser"
        )
    );
document.addEventListener("DOMContentLoaded", () => {

    if(loggedUser.role === "ADVISOR"){

        document.getElementById(
            "addPortfolioBtn"
        ).style.display = "none";
    }

});

loadPortfolios();
loadInvestors();

document.getElementById(
    "portfolioForm"
).addEventListener(
    "submit",
    savePortfolio
);

function openModal(){

    document.getElementById(
        "portfolioModal"
    ).style.display = "block";
}

function closeModal(){

    document.getElementById(
        "portfolioModal"
    ).style.display = "none";

    document.getElementById(
        "portfolioForm"
    ).reset();

    document.getElementById(
        "portfolioForm"
    ).removeAttribute("data-id");
}

function loadInvestors(){

    fetch(
        "http://localhost:8083/auth/users"
    )

    .then(response => response.json())

    .then(users => {

        let options =
            '<option value="">Select Investor</option>';

        users.forEach(user => {

            if(user.role === "INVESTOR"){

                options += `

                <option value="${user.userId}">
                    ${user.username}
                </option>

                `;
            }
        });

        const investorSelect =
            document.getElementById(
                "investorId"
            );

        investorSelect.innerHTML =
            options;

        if(loggedUser.role === "INVESTOR"){

            investorSelect.value =
                loggedUser.userId;

            investorSelect.style.display =
                "none";
        }
    });
}

async function savePortfolio(event){

    event.preventDefault();

    const portfolioId =
        document.getElementById(
            "portfolioForm"
        ).getAttribute("data-id");

    const portfolio = {

        portfolioId:
            portfolioId
            ? Number(portfolioId)
            : null,

        portfolioName:
            document.getElementById(
                "portfolioName"
            ).value,

        riskLevel:
            document.getElementById(
                "riskLevel"
            ).value,

        totalValue: 0,

        investor:{
            userId:
                loggedUser.role === "INVESTOR"
                ? loggedUser.userId
                : Number(
                    document.getElementById(
                        "investorId"
                    ).value
                )
        }
    };

    const url =
        portfolioId
            ? `${API_URL}/update`
            : `${API_URL}/add`;

    const method =
        portfolioId
            ? "PUT"
            : "POST";

    const response = await fetch(url,{

        method:method,

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify(portfolio)
    });

    if(!response.ok){

        alert("Failed to save portfolio");
        return;
    }

    closeModal();

    loadPortfolios();
}

async function loadPortfolios(){

    const response =
        await fetch(
            `${API_URL}/all`
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

    let container =
        document.getElementById(
            "portfolioContainer"
        );

    container.innerHTML = "";

    let high=0;
    let medium=0;
    let low=0;

    if(portfolios.length === 0){

        document.getElementById(
            "portfolioContainer"
        ).innerHTML = "";

        document.getElementById(
            "emptyState"
        ).innerHTML = `

            <div class="empty-state">

                <i class="fas fa-wallet"></i>

                <h3>
                    No Portfolios Found
                </h3>

                <p>
                    Create your first portfolio to get started.
                </p>

            </div>

        `;

        document.getElementById(
            "totalPortfolioCount"
        ).textContent = 0;

        document.getElementById(
            "highRiskCount"
        ).textContent = 0;

        document.getElementById(
            "mediumRiskCount"
        ).textContent = 0;

        document.getElementById(
            "lowRiskCount"
        ).textContent = 0;

        return;
    }

    portfolios.sort((a,b) =>
        a.investor.username.localeCompare(
            b.investor.username
        )
    );


    // Count risk levels

    portfolios.forEach(portfolio => {

        if(portfolio.riskLevel === "HIGH") high++;

        if(portfolio.riskLevel === "MEDIUM") medium++;

        if(portfolio.riskLevel === "LOW") low++;
    });

    // Group by investor

    const grouped = {};

    portfolios.forEach(portfolio => {

        const investorName =
            portfolio.investor.username;

        if(!grouped[investorName]){

            grouped[investorName] = [];
        }

        grouped[investorName].push(portfolio);
    });

    container.innerHTML = "";

    // Render grouped portfolios

    for(const investorName in grouped){

        if(loggedUser.role === "ADMIN" ||
            loggedUser.role === "ADVISOR"){

            container.innerHTML += `

            <div class="investor-header">

                <i class="fas fa-user"></i>

                Investor : ${investorName}

            </div>

            `;
        }

        grouped[investorName].forEach(portfolio => {

            const riskClass =
                portfolio.riskLevel.toLowerCase();

            container.innerHTML += `

            <div class="portfolio-card">

                <h3>
                    ${portfolio.portfolioName}
                </h3>

                <div class="portfolio-row">

                    <span>Portfolio ID</span>

                    <strong>
                        ${portfolio.portfolioId}
                    </strong>

                </div>

                <div class="portfolio-row">

                    <span>Risk Level</span>

                    <span class="risk-badge ${riskClass}">
                        ${portfolio.riskLevel}
                    </span>

                </div>

                <div class="portfolio-row">

                    <span>Total Value</span>

                    <strong>
                        ₹${portfolio.totalValue ?? 0}
                    </strong>

                </div>

                ${
                loggedUser.role === "ADMIN" ||
                loggedUser.role === "INVESTOR"
                ?
                `
                <div class="actions">

                    <button
                        class="update-btn"
                        onclick="editPortfolio(${portfolio.portfolioId})">

                        Update

                    </button>

                    <button
                        class="delete-btn"
                        onclick="deletePortfolio(${portfolio.portfolioId})">

                        Delete

                    </button>

                </div>
                `
                :
                ""
                }

            </div>

            `;
        });
    }

    document.getElementById(
        "totalPortfolioCount"
    ).textContent =
        portfolios.length;

    document.getElementById(
        "highRiskCount"
    ).textContent = high;

    document.getElementById(
        "mediumRiskCount"
    ).textContent = medium;

    document.getElementById(
        "lowRiskCount"
    ).textContent = low;

}

async function editPortfolio(id){

    console.log("Editing:", id);

    const response =
        await fetch(`${API_URL}/${id}`);

    const portfolio =
        await response.json();

    console.log(portfolio);

    document.getElementById("portfolioName").value =
        portfolio.portfolioName;

    document.getElementById("riskLevel").value =
        portfolio.riskLevel;

    document.getElementById("investorId").value =
        portfolio.investor.userId;

    document.getElementById("portfolioForm")
        .setAttribute("data-id", id);

    openModal();
}

async function deletePortfolio(id){

    if(!confirm("Delete Portfolio?")){
        return;
    }

    const response = await fetch(
        `${API_URL}/${id}`,
        {
            method:"DELETE"
        }
    );

    const message = await response.text();

    alert(message);

    if(response.ok){

        loadPortfolios();
    }
}
function searchPortfolio(){

    let input =
        document.getElementById(
            "searchPortfolio"
        ).value.toLowerCase();

    document
        .querySelectorAll(
            ".portfolio-card"
        )
        .forEach(card => {

            const name =
                card.querySelector("h3")
                    .textContent
                    .toLowerCase();

            card.style.display =
                name.includes(input)
                    ? "block"
                    : "none";
        });
}