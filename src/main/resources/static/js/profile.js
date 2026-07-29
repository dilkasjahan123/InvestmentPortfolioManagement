document.addEventListener("DOMContentLoaded", () => {
    const menuButton = document.getElementById("menuButton");
    const sidebarOverlay = document.getElementById("sidebarOverlay");
    const passwordToggle = document.getElementById("passwordToggle");
    const passwordSection = document.getElementById("passwordSection");
    const profileForm = document.querySelector(".profile-form");
    const currentPassword = document.getElementById("currentPassword");
    const newPassword = document.getElementById("newPassword");
    const confirmPassword = document.getElementById("confirmPassword");
    const clientPasswordError = document.getElementById("clientPasswordError");

    function closeSidebar() {
        document.body.classList.remove("sidebar-open");
    }

    function showPasswordError(message) {
        if (!clientPasswordError) {
            return;
        }

        clientPasswordError.textContent = message;
        clientPasswordError.hidden = false;
    }

    function clearPasswordError() {
        if (!clientPasswordError) {
            return;
        }

        clientPasswordError.textContent = "";
        clientPasswordError.hidden = true;
    }

    menuButton?.addEventListener("click", () => {
        document.body.classList.toggle("sidebar-open");
    });

    sidebarOverlay?.addEventListener("click", closeSidebar);

    passwordToggle?.addEventListener("click", () => {
        const isOpening = passwordSection?.hasAttribute("hidden");

        if (isOpening) {
            passwordSection?.removeAttribute("hidden");
            passwordToggle.setAttribute("aria-expanded", "true");
            currentPassword?.focus();
        } else {
            passwordSection?.setAttribute("hidden", "");
            passwordToggle.setAttribute("aria-expanded", "false");
            clearPasswordError();
        }
    });

    document.querySelectorAll("[data-password-target]").forEach(button => {
        button.addEventListener("click", () => {
            const targetId = button.dataset.passwordTarget;
            const input = document.getElementById(targetId);
            const icon = button.querySelector("i");

            if (!input) {
                return;
            }

            const isPassword = input.type === "password";
            input.type = isPassword ? "text" : "password";
            button.setAttribute(
                "aria-label",
                `${isPassword ? "Hide" : "Show"} password`
            );
            icon?.classList.toggle("fa-eye", !isPassword);
            icon?.classList.toggle("fa-eye-slash", isPassword);
        });
    });

    profileForm?.addEventListener("submit", event => {
        clearPasswordError();

        const currentValue = currentPassword?.value || "";
        const newValue = newPassword?.value || "";
        const confirmValue = confirmPassword?.value || "";
        const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/;

        if (!newValue && !currentValue && !confirmValue) {
            return;
        }

        if (!currentValue) {
            event.preventDefault();
            showPasswordError("Enter your current password to set a new password.");
            currentPassword?.focus();
            return;
        }

        if (!passwordPattern.test(newValue)) {
            event.preventDefault();
            showPasswordError(
                "Your new password needs an uppercase letter, lowercase letter, number, and at least 6 characters."
            );
            newPassword?.focus();
            return;
        }

        if (newValue !== confirmValue) {
            event.preventDefault();
            showPasswordError("The new password and confirmation do not match.");
            confirmPassword?.focus();
        }
    });

    window.addEventListener("resize", () => {
        if (window.innerWidth > 900) {
            closeSidebar();
        }
    });
});
