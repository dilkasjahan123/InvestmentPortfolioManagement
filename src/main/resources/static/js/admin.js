const user =
    JSON.parse(
        localStorage.getItem(
            "loggedUser"
        )
    );

if(user){

    document.getElementById(
        "username"
    ).textContent =
        user.username;
}

loadDashboardCounts();

function loadDashboardCounts(){

    fetch(
        "http://localhost:8083/auth/count"
    )
    .then(response => response.json())
    .then(data => {

        document.getElementById(
            "userCount"
        ).textContent = data;
    });

    fetch(
        "http://localhost:8083/portfolio/count"
    )
    .then(response => response.json())
    .then(data => {

        document.getElementById(
            "portfolioCount"
        ).textContent = data;
    });

    fetch(
        "http://localhost:8083/assets/count"
    )
    .then(response => response.json())
    .then(data => {

        document.getElementById(
            "assetCount"
        ).textContent = data;
    });

    fetch(
        "http://localhost:8083/transaction/count"
    )
    .then(response => response.json())
    .then(data => {

        document.getElementById(
            "transactionCount"
        ).textContent = data;
    });
}

function goToUsers(){

    window.location.href =
        "users.html";
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