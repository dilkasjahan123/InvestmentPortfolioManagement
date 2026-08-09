document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("registerForm");
    const registerButton = document.getElementById("registerButton");
    const username = document.getElementById("username");
    const email = document.getElementById("email");
    const role = document.getElementById("role");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");

    const requirements = {
        length: value => value.length >= 6,
        uppercase: value => /[A-Z]/.test(value),
        lowercase: value => /[a-z]/.test(value),
        number: value => /\d/.test(value)
    };

    // Display validation error message
    function setError(input, errorId, message) {
        const error = document.getElementById(errorId);
        const fieldControl = input?.closest(".field-control");

        if (error) {
            error.textContent = message;
        }

        fieldControl?.classList.toggle("invalid", Boolean(message));
    }

    // Update password strength checklist
    function updatePasswordChecklist() {
        const value = password?.value || "";

        Object.entries(requirements).forEach(([name, test]) => {
            const item = document.querySelector(
                `[data-requirement="${name}"]`
            );
            item?.classList.toggle("met", test(value));
        });
    }

    // Check if password meets all requirements
    function passwordIsValid(value) {
        return Object.values(requirements).every(test => test(value));
    }

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

    // Clear username validation error while typing
    username?.addEventListener("input", () => {
        setError(username, "usernameError", "");
    });

    // Clear email validation error while typing
    email?.addEventListener("input", () => {
        setError(email, "emailError", "");
    });

    role?.addEventListener("change", () => {
        setError(role, "roleError", "");
    });

    password?.addEventListener("input", () => {
        setError(password, "passwordError", "");
        updatePasswordChecklist();
    });

    confirmPassword?.addEventListener("input", () => {
        setError(confirmPassword, "confirmPasswordError", "");
    });

    // Validate form before submission
    registerForm?.addEventListener("submit", event => {
        let isValid = true;
        const usernameValue = username?.value.trim() || "";
        const passwordValue = password?.value || "";
        const confirmValue = confirmPassword?.value || "";
        const usernamePattern = /^[A-Za-z0-9._-]{3,30}$/;

        setError(username, "usernameError", "");
        setError(email, "emailError", "");
        setError(role, "roleError", "");
        setError(password, "passwordError", "");
        setError(confirmPassword, "confirmPasswordError", "");

        if (!usernamePattern.test(usernameValue)) {
            setError(
                username,
                "usernameError",
                "Use 3–30 letters, numbers, dots, underscores, or hyphens."
            );
            isValid = false;
        }

        if (!email?.value || !email.checkValidity()) {
            setError(
                email,
                "emailError",
                "Enter a valid email address."
            );
            isValid = false;
        }

        if (!role?.value) {
            setError(
                role,
                "roleError",
                "Choose an investor or advisor account."
            );
            isValid = false;
        }

        if (!passwordIsValid(passwordValue)) {
            setError(
                password,
                "passwordError",
                "Your password does not meet all requirements."
            );
            isValid = false;
        }

        if (!confirmValue || passwordValue !== confirmValue) {
            setError(
                confirmPassword,
                "confirmPasswordError",
                "The passwords do not match."
            );
            isValid = false;
        }

        if (!isValid) {
            event.preventDefault();

            const firstInvalid = registerForm.querySelector(".invalid input, .invalid select");
            firstInvalid?.focus();
            return;
        }

        // Disable button to prevent duplicate submission
        if (registerButton) {
            registerButton.disabled = true;
            registerButton.setAttribute("aria-busy", "true");
            registerButton.querySelector("span").textContent =
                "Creating account...";
            registerButton.querySelector("i").className =
                "fa-solid fa-circle-notch fa-spin";
        }
    });

    updatePasswordChecklist();
});
