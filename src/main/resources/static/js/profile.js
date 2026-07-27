const loggedUser =
    JSON.parse(
        localStorage.getItem(
            "loggedUser"
        )
    );

document.addEventListener(
    "DOMContentLoaded",
    () => {

        if(loggedUser.role === "ADVISOR"){

            document.getElementById(
                "usersMenu"
            ).style.display = "none";

            document.getElementById(
                "transactionsMenu"
            ).style.display = "none";
        }

        if(loggedUser.role === "INVESTOR"){

            document.getElementById(
                "usersMenu"
            ).style.display = "none";
        }

    }
);

loadProfile();

function loadProfile(){

    fetch(
        `http://localhost:8083/auth/profile/${loggedUser.userId}`
    )
    .then(response => response.json())
    .then(user => {

        document.getElementById(
            "profileName"
        ).textContent =
            user.username;

        document.getElementById(
            "username"
        ).value =
            user.username;

        document.getElementById(
            "email"
        ).value =
            user.email;

        document.getElementById(
            "role"
        ).textContent =
            user.role;
    });
}

function updateProfile(){

    const currentPassword =
        document.getElementById(
            "currentPassword"
        ).value;

    const newPassword =
        document.getElementById(
            "newPassword"
        ).value;

    const confirmPassword =
        document.getElementById(
            "confirmPassword"
        ).value;

    if(
        newPassword !==
        confirmPassword
    ){

        alert(
            "Passwords do not match"
        );

        return;
    }

    const updatedUser = {

        userId: loggedUser.userId,

        username:
            document.getElementById(
                "username"
            ).value,

        email:
            document.getElementById(
                "email"
            ).value,

        password:
            document.getElementById(
                "newPassword"
            ).value
    };


    fetch(
        "http://localhost:8083/auth/profile/update",
        {
            method:"PUT",

            headers:{
                "Content-Type":
                "application/json"
            },

            body:
                JSON.stringify(
                    updatedUser
                )
        }
    )
    .then(response => response.json())
    .then(data => {

        localStorage.setItem(
            "loggedUser",
            JSON.stringify(data)
        );

        alert(
            "Profile Updated Successfully"
        );
    });
}

function goToDashboard(){

    if(loggedUser.role === "ADMIN"){

        window.location.href =
            "admin.html";

    }
    else if(loggedUser.role === "ADVISOR"){

        window.location.href =
            "advisor.html";

    }
    else{

        window.location.href =
            "investor-dashboard.html";

    }
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

function logout(){

    localStorage.removeItem(
        "loggedUser"
    );

    window.location.href =
        "login.html";
}
function togglePasswordSection(){

    const section =
        document.getElementById(
            "passwordSection"
        );

    if(section.style.display === "block"){

        section.style.display =
            "none";
    }

    else{

        section.style.display =
            "block";
    }
}
function togglePassword(
    inputId,
    icon
){

    const input =
        document.getElementById(
            inputId
        );

    if(input.type === "password"){

        input.type = "text";

        icon.classList.remove(
            "fa-eye"
        );

        icon.classList.add(
            "fa-eye-slash"
        );
    }
    else{

        input.type = "password";

        icon.classList.remove(
            "fa-eye-slash"
        );

        icon.classList.add(
            "fa-eye"
        );
    }
}