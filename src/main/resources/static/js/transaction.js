const API_URL =
    "http://localhost:8083/transaction";

loadTransactions();
loadAssets();

document
    .getElementById(
        "transactionForm"
    )
    .addEventListener(
        "submit",
        saveTransaction
    );

function openModal(){

    document
        .getElementById(
            "transactionModal"
        )
        .style.display = "block";
}

function closeModal(){

    document
        .getElementById(
            "transactionModal"
        )
        .style.display = "none";

    document
        .getElementById(
            "transactionForm"
        )
        .reset();
}
function loadAssets(){

    fetch(
        "http://localhost:8083/asset/all"
    )

    .then(response => response.json())

    .then(data => {

        let options =
            '<option value="">Select Asset</option>';

        data.forEach(asset => {

            options += `

                <option
                    value="${asset.assetId}">

                    ${asset.assetName}

                </option>
            `;
        });

        document
            .getElementById(
                "assetId"
            )
            .innerHTML = options;
    });
}
async function saveTransaction(event){

    event.preventDefault();

    const transaction = {

        asset:{
            assetId:Number(
                document.getElementById(
                    "assetId"
                ).value
            )
        },

        transactionType:
            document.getElementById(
                "transactionType"
            ).value,

        quantity:Number(
            document.getElementById(
                "quantity"
            ).value
        ),

        price:Number(
            document.getElementById(
                "price"
            ).value
        )
    };

    const response =
        await fetch(
            `${API_URL}/add`,
            {
                method:"POST",
                headers:{
                    "Content-Type":
                        "application/json"
                },
                body:JSON.stringify(
                    transaction
                )
            }
        );

    if(response.ok){

        alert(
            "Transaction Added"
        );

        closeModal();

        loadTransactions();
    }
}
async function loadTransactions(){

    const response =
        await fetch(
            `${API_URL}/all`
        );

    const transactions =
        await response.json();

    const container =
        document.getElementById(
            "transactionContainer"
        );

    container.innerHTML = "";

    const grouped = {};

    transactions.forEach(t => {

        const investorName =
            t.asset.portfolio.investor.username;

        if(!grouped[investorName]){

            grouped[investorName] = [];
        }

        grouped[investorName].push(t);
    });

    for(const investorName in grouped){

        container.innerHTML += `

            <div class="investor-header">

                <i class="fas fa-user"></i>

                Investor : ${investorName}

            </div>

        `;

        grouped[investorName].forEach(t => {

            container.innerHTML += `

            <div class="transaction-card">

                <h3>
                    ${t.asset.assetName}
                </h3>

                <p>
                    Portfolio :
                    ${t.asset.portfolio.portfolioName}
                </p>

                <p>
                    Type :
                    <span class="${
                        t.transactionType === "BUY"
                        ? "buy"
                        : "sell"
                    }">
                        ${t.transactionType}
                    </span>
                </p>

                <p>
                    Status :
                    <span class="${
                        t.status === "SUCCESS"
                        ? "success"
                        : "failed"
                    }">
                        ${t.status}
                    </span>
                </p>

                <p>
                    Quantity :
                    ${t.quantity}
                </p>

                <p>
                    Price :
                    ₹${t.price}
                </p>

                <p>
                    Date :
                    ${new Date(
                        t.transactionDate
                    ).toLocaleString()}
                </p>

            </div>

            `;
        });
    }
}
function searchTransaction(){

    let input =
        document
            .getElementById(
                "searchInput"
            )
            .value
            .toLowerCase();

    document
        .querySelectorAll(
            ".transaction-card"
        )
        .forEach(card => {

            const asset =
                card.querySelector("h3")
                    .textContent
                    .toLowerCase();

            card.style.display =
                asset.includes(input)
                    ? "block"
                    : "none";
        });
}