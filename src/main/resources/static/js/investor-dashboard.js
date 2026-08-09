document.addEventListener("DOMContentLoaded", () => {
    initializeSidebar();
    initializePortfolioSearch();
});

// Mobile sidebar controls
function initializeSidebar() {
    const menuButton =
            document.getElementById("menuButton");

    const sidebar =
            document.getElementById("dashboardSidebar");

    const overlay =
            document.getElementById("sidebarOverlay");

    if (!menuButton || !sidebar || !overlay) {
        return;
    }

    const closeSidebar = () => {
        sidebar.classList.remove("open");
        overlay.classList.remove("visible");
        document.body.classList.remove("sidebar-open");
        menuButton.setAttribute("aria-expanded", "false");
    };

    menuButton.addEventListener("click", () => {
        const shouldOpen =
                !sidebar.classList.contains("open");

        sidebar.classList.toggle("open", shouldOpen);
        overlay.classList.toggle("visible", shouldOpen);
        document.body.classList.toggle(
                "sidebar-open",
                shouldOpen
        );
        menuButton.setAttribute(
                "aria-expanded",
                String(shouldOpen)
        );
    });

    overlay.addEventListener("click", closeSidebar);

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeSidebar();
        }
    });
}

// Portfolio search filtering
function initializePortfolioSearch() {
    const searchInput =
            document.getElementById("portfolioSearch");

    if (!searchInput) {
        return;
    }

    searchInput.addEventListener("input", () => {
        const searchTerm =
                searchInput.value.trim().toLowerCase();

        document
            .querySelectorAll(".portfolio-card")
            .forEach((card) => {
                const cardText =
                        card.textContent.toLowerCase();

                card.hidden =
                        !cardText.includes(searchTerm);
            });
    });
}
