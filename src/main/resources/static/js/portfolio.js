document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("searchPortfolio");

    if (!searchInput) {
        return;
    }

    searchInput.addEventListener("input", () => {
        const searchTerm = searchInput.value.trim().toLowerCase();

        document.querySelectorAll(".investor-group").forEach((group) => {
            const investorText =
                    group.querySelector(".investor-header")
                        ?.textContent.toLowerCase() ?? "";

            const investorMatches = investorText.includes(searchTerm);
            let hasVisiblePortfolio = false;

            group.querySelectorAll(".portfolio-card").forEach((card) => {
                const portfolioName =
                        card.querySelector("h3")
                            ?.textContent.toLowerCase() ?? "";

                const matches =
                        investorMatches || portfolioName.includes(searchTerm);

                card.hidden = !matches;
                hasVisiblePortfolio = hasVisiblePortfolio || matches;
            });

            group.hidden = !hasVisiblePortfolio;
        });
    });
});
