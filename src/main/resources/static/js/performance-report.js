const USER_API =
    "http://localhost:8083/auth/users";

const PORTFOLIO_API =
    "http://localhost:8083/portfolio/all";

const ASSET_API =
    "http://localhost:8083/assets/all";

const TRANSACTION_API =
    "http://localhost:8083/transaction/all";

const ANALYTICS_API =
    "http://localhost:8083/analytics";



let allUsers = [];
let allPortfolios = [];
let allAssets = [];
let allTransactions = [];

let performanceChart;
let valueChart;
let riskChart;
let allocationChart;

let assetPerformanceChart;
let investmentChart;
let buySellChart;


loadDashboard();


async function loadDashboard(){

    await loadUsers();

    allPortfolios =
        await fetch(PORTFOLIO_API)
            .then(r => r.json());

    allAssets =
        await fetch(ASSET_API)
            .then(r => r.json());

    allTransactions =
        await fetch(TRANSACTION_API + "/all")
            .then(r => r.json());
}

async function loadUsers(){

    allUsers =
        await fetch(USER_API)
            .then(r => r.json());

    let options =
        `<option value="">
            Select User
        </option>`;

    allUsers.forEach(user => {

        options += `

        <option value="${user.userId}">
            ${user.username}
        </option>

        `;
    });

    document
        .getElementById(
            "userSelect"
        )
        .innerHTML = options;
}

document
    .getElementById(
        "userSelect"
    )
    .addEventListener(
        "change",
        loadUserAnalytics
    );

async function loadUserAnalytics(){

    const userId =
        document.getElementById(
            "userSelect"
        ).value;

    if(!userId){
        return;
    }

    const portfolios =
        allPortfolios.filter(
            p => p.investor.userId == userId
        );

    let totalAssets = 0;
    let totalValue = 0;
    let totalReturn = 0;

    let portfolioNames = [];
    let performanceValues = [];
    let portfolioValues = [];

    let lowRisk = 0;
    let mediumRisk = 0;
    let highRisk = 0;

    let stockCount = 0;
    let bondCount = 0;
    let mutualCount = 0;

    for(const portfolio of portfolios){

        const assets =
            allAssets.filter(
                a =>
                    a.portfolio.portfolioId
                    ===
                    portfolio.portfolioId
            );

        totalAssets += assets.length;

        assets.forEach(asset => {

            totalValue +=
                asset.currentPrice
                *
                asset.quantity;

            if(
                asset.assetType
                ===
                "Stock"
            ){

                stockCount++;
            }

            else if(
                asset.assetType
                ===
                "Bond"
            ){

                bondCount++;
            }

            else{

                mutualCount++;
            }
        });

        const performance =
            await fetch(
                `${ANALYTICS_API}/portfolio/${portfolio.portfolioId}`
            )
            .then(r => r.json());

        const risk =
            await fetch(
                `${ANALYTICS_API}/risk/${portfolio.portfolioId}`
            )
            .then(r => r.json());

        totalReturn += performance;

        portfolioNames.push(
            portfolio.portfolioName
        );

        performanceValues.push(
            performance
        );

        portfolioValues.push(
            portfolio.totalValue
        );

        if(risk <= 3){

            lowRisk++;
        }

        else if(risk <= 6){

            mediumRisk++;
        }

        else{

            highRisk++;
        }
    }

    document
        .getElementById(
            "portfolioCount"
        )
        .textContent =
        portfolios.length;

    document
        .getElementById(
            "assetCount"
        )
        .textContent =
        totalAssets;

    document
        .getElementById(
            "portfolioValue"
        )
        .textContent =
        "₹" +
        totalValue.toFixed(2);

    document
        .getElementById(
            "avgReturn"
        )
        .textContent =
        portfolios.length > 0
        ?
        (
            totalReturn
            /
            portfolios.length
        ).toFixed(2)
        +
        "%"
        :
        "0%";

    renderPortfolioCards(
        portfolios
    );

    createPerformanceChart(
        portfolioNames,
        performanceValues
    );

    createValueChart(
        portfolioNames,
        portfolioValues
    );

    createRiskChart(
        lowRisk,
        mediumRisk,
        highRisk
    );

    createAllocationChart(
        stockCount,
        bondCount,
        mutualCount
    );
}

function renderPortfolioCards(
    portfolios
){

    const container =
        document.getElementById(
            "portfolioContainer"
        );

    container.innerHTML = "";

    portfolios.forEach(
        async portfolio => {

        const performance =
            await fetch(
                `${ANALYTICS_API}/portfolio/${portfolio.portfolioId}`
            )
            .then(r => r.json());

        const risk =
            await fetch(
                `${ANALYTICS_API}/risk/${portfolio.portfolioId}`
            )
            .then(r => r.json());

        container.innerHTML += `

        <div class="portfolio-card"
             onclick="viewPortfolioDetails(${portfolio.portfolioId})">

            <h3>
                ${portfolio.portfolioName}
            </h3>

            <p>
                Return :
                ${Number(
                    performance
                ).toFixed(2)}%
            </p>

            <p>
                Risk Score :
                ${risk}
            </p>

            <p>
                Value :
                ₹${portfolio.totalValue}
            </p>

            <button>

                View Details

            </button>

        </div>

        `;
    });
}

function createPerformanceChart(
    labels,
    values
){

    if(performanceChart){

        performanceChart.destroy();
    }

    performanceChart =
        new Chart(

            document
            .getElementById(
                "performanceChart"
            ),

            {

                type:"bar",

                data:{

                    labels,

                    datasets:[{

                        label:
                        "Return %",

                        data:values,

                        backgroundColor:
                        "#2563eb"
                    }]
                }
            }
        );
}

function createValueChart(
    labels,
    values
){

    if(valueChart){

        valueChart.destroy();
    }

    valueChart =
        new Chart(

            document
            .getElementById(
                "valueChart"
            ),

            {

                type:"bar",

                data:{

                    labels,

                    datasets:[{

                        label:
                        "Portfolio Value",

                        data:values,

                        backgroundColor:
                        "#10b981"
                    }]
                }
            }
        );
}

function createRiskChart(
    low,
    medium,
    high
){

    if(riskChart){

        riskChart.destroy();
    }

    riskChart =
        new Chart(

            document
            .getElementById(
                "riskChart"
            ),

            {

                type:"doughnut",

                data:{

                    labels:[
                        "Low",
                        "Medium",
                        "High"
                    ],

                    datasets:[{

                        data:[
                            low,
                            medium,
                            high
                        ]
                    }]
                }
            }
        );
}

function createAllocationChart(
    stocks,
    bonds,
    mutualFunds
){

    if(allocationChart){

        allocationChart.destroy();
    }

    allocationChart =
        new Chart(

            document
            .getElementById(
                "allocationChart"
            ),

            {

                type:"pie",

                data:{

                    labels:[
                        "Stocks",
                        "Bonds",
                        "Mutual Funds"
                    ],

                    datasets:[{

                        data:[
                            stocks,
                            bonds,
                            mutualFunds
                        ]
                    }]
                }
            }
        );
}

function closeModal(){

    document
        .getElementById(
            "portfolioModal"
        )
        .style.display =
        "none";
}
async function viewPortfolioDetails(
    portfolioId
){

    document.getElementById(
        "portfolioModal"
    ).style.display =
        "block";

    const portfolio =
        allPortfolios.find(
            p =>
                p.portfolioId ==
                portfolioId
        );

    document.getElementById(
        "portfolioTitle"
    ).innerHTML =

        `${portfolio.portfolioName}
        - ${portfolio.investor.username}`;

    const assets =
        allAssets.filter(
            asset =>
                asset.portfolio.portfolioId ==
                portfolioId
        );

    renderAssetDetails(
        assets
    );

    createAssetPerformanceChart(
        assets
    );

    createInvestmentChart(
        assets
    );

    createBuySellChart(
        portfolioId
    );
}
function renderAssetDetails(
    assets
){

    let html = `

    <table class="asset-table">

        <tr>

            <th>Asset</th>

            <th>Quantity</th>

            <th>Purchase Price</th>

            <th>Current Price</th>

            <th>Performance</th>

        </tr>

    `;

    assets.forEach(asset => {

        const performance =

            (
                (
                    asset.currentPrice
                    -
                    asset.purchasePrice
                )
                /
                asset.purchasePrice
            ) * 100;

        html += `

        <tr>

            <td>${asset.assetName}</td>

            <td>${asset.quantity}</td>

            <td>₹${asset.purchasePrice}</td>

            <td>₹${asset.currentPrice}</td>

            <td>${performance.toFixed(2)}%</td>

        </tr>

        `;
    });

    html += `</table>`;

    document.getElementById(
        "assetDetails"
    ).innerHTML = html;
}
function createAssetPerformanceChart(
    assets
){

    const labels = [];
    const values = [];

    assets.forEach(asset => {

        labels.push(
            asset.assetName
        );

        values.push(

            (
                (
                    asset.currentPrice
                    -
                    asset.purchasePrice
                )
                /
                asset.purchasePrice
            ) * 100
        );
    });

    if(assetPerformanceChart){

        assetPerformanceChart.destroy();
    }

    assetPerformanceChart =
        new Chart(

            document
                .getElementById(
                    "assetPerformanceChart"
                ),

            {

                type:"bar",

                data:{

                    labels:labels,

                    datasets:[{

                        label:
                            "Asset Performance %",

                        data:values,

                        backgroundColor:
                            "#2563eb"
                    }]
                }
            }
        );
}
function createInvestmentChart(
    assets
){

    let invested = 0;
    let current = 0;

    assets.forEach(asset => {

        invested +=
            asset.purchasePrice *
            asset.quantity;

        current +=
            asset.currentPrice *
            asset.quantity;
    });

    if(investmentChart){

        investmentChart.destroy();
    }

    investmentChart =
        new Chart(

            document
                .getElementById(
                    "investmentChart"
                ),

            {

                type:"doughnut",

                data:{

                    labels:[
                        "Invested",
                        "Current"
                    ],

                    datasets:[{

                        data:[
                            invested,
                            current
                        ]
                    }]
                }
            }
        );
}
function createBuySellChart(
    portfolioId
){

    let buyCount = 0;
    let sellCount = 0;

    const assets =
        allAssets.filter(
            a =>
                a.portfolio.portfolioId
                ==
                portfolioId
        );

    const assetIds =
        assets.map(
            a => a.assetId
        );

    allTransactions.forEach(
        transaction => {

        if(
            assetIds.includes(
                transaction.asset.assetId
            )
        ){

            if(
                transaction.transactionType
                === "BUY"
            ){

                buyCount++;
            }

            else{

                sellCount++;
            }
        }
    });

    if(buySellChart){

        buySellChart.destroy();
    }

    buySellChart =
        new Chart(

            document
                .getElementById(
                    "buySellChart"
                ),

            {

                type:"pie",

                data:{

                    labels:[
                        "BUY",
                        "SELL"
                    ],

                    datasets:[{

                        data:[
                            buyCount,
                            sellCount
                        ]
                    }]
                }
            }
        );
}