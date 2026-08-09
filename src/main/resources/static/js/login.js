document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const loginButton = document.getElementById("loginButton");

    // Toggle password visibility
    document.querySelectorAll("[data-password-target]").forEach(button => {
        button.addEventListener("click", () => {
            const input = document.getElementById(button.dataset.passwordTarget);
            const icon = button.querySelector("i");

            if (!input) {
                return;
            }

            const willShow = input.type === "password";
            input.type = willShow ? "text" : "password";
            button.setAttribute(
                "aria-label",
                willShow ? "Hide password" : "Show password"
            );
            icon?.classList.toggle("fa-eye", !willShow);
            icon?.classList.toggle("fa-eye-slash", willShow);
        });
    });

    // Validate form before submission
    loginForm?.addEventListener("submit", event => {
        // Check required fields
        if (!loginForm.checkValidity()) {
            event.preventDefault();
            loginForm.reportValidity();
            return;
        }

        // Prevent multiple submissions and show loading state
        if (loginButton) {
            loginButton.disabled = true;
            loginButton.setAttribute("aria-busy", "true");
            loginButton.querySelector("span").textContent = "Signing in...";
            loginButton.querySelector("i").className =
                "fa-solid fa-circle-notch fa-spin";
        }
    });
});
