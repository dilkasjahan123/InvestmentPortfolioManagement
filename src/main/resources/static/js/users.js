loadUsers();

function loadUsers() {

    fetch(
        "http://localhost:8083/auth/users"
    )

    .then(response => response.json())

    .then(data => {

        let rows = "";

        let advisorCount = 0;
        let investorCount = 0;

        data.forEach(user => {

            if(user.role === "ADVISOR"){
                advisorCount++;
            }

            if(user.role === "INVESTOR"){
                investorCount++;
            }

            rows += `
            <tr>
                <td>${user.userId}</td>
                <td>${user.username}</td>
                <td>${user.email}</td>

                <td>
                    <span class="
                    ${user.role === 'ADVISOR'
                        ? 'advisor'
                        : 'investor'} badge">

                        ${user.role}

                    </span>
                </td>

                <td>
                    <button
                        class="delete-btn"
                        onclick="deleteUser(${user.userId})">
                        Delete
                    </button>
                </td>
            </tr>
            `;
        });

        document.getElementById(
            "advisorCount"
        ).textContent = advisorCount;

        document.getElementById(
            "investorCount"
        ).textContent = investorCount;

        document.getElementById(
            "userTable"
        ).innerHTML = rows;
    })

    .catch(error => {

        console.error(error);

        alert("Failed to load users");
    });
}

function deleteUser(id) {

    if (!confirm(
        "Are you sure you want to delete this user?"
    )) {
        return;
    }

    fetch(
        `http://localhost:8083/auth/user/${id}`,
        {
            method: "DELETE"
        }
    )

    .then(response => response.text())

    .then(message => {

        alert(message);

        loadUsers();
    })

    .catch(error => {

        console.error(error);

        alert("Delete failed");
    });
}
function searchUser() {

    let input =
        document.getElementById(
            "searchUser"
        ).value.toLowerCase();

    let rows =
        document.querySelectorAll(
            "#userTable tr"
        );

    rows.forEach(row => {

        let username =
            row.cells[1]
            .textContent
            .toLowerCase();

        if (
            username.includes(input)
        ) {

            row.style.display = "";

        } else {

            row.style.display = "none";
        }

    });
}