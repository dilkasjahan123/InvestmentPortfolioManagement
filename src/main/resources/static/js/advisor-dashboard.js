document.addEventListener("DOMContentLoaded", () => {
    initializeAdvisorSidebar();
    initializeInvestorSearch();
});

function initializeAdvisorSidebar() {
    const menuButton =
            document.getElementById("menuButton");

    const sidebar =
            document.getElementById("advisorSidebar");

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

function initializeInvestorSearch() {
    const searchInput =
            document.getElementById("investorSearch");

    if (!searchInput) {
        return;
    }

    searchInput.addEventListener("input", () => {
        const searchTerm =
                searchInput.value.trim().toLowerCase();

        document
            .querySelectorAll(".investor-card")
            .forEach((card) => {
                const cardText =
                        card.textContent.toLowerCase();

                card.hidden =
                        !cardText.includes(searchTerm);
            });
    });
}
