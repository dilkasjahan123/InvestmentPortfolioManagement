document.addEventListener("DOMContentLoaded", () => {
    initializeAssetSearch();
});

// Asset search and filtering
function initializeAssetSearch() {
    const searchInput = document.getElementById("assetSearch");

    if (!searchInput) {
        return;
    }

// Filter portfolio groups and assets based on search text
    searchInput.addEventListener("input", () => {
        const searchTerm = searchInput.value.trim().toLowerCase();

        document.querySelectorAll(".portfolio-group").forEach((group) => {
            const groupText =
                    group.querySelector(".portfolio-group-header")
                        ?.textContent.toLowerCase() ?? "";

            const groupMatches = groupText.includes(searchTerm);
            let hasVisibleAsset = false;

            group.querySelectorAll(".asset-card").forEach((card) => {
                const cardText = card.textContent.toLowerCase();
                const matches =
                        groupMatches || cardText.includes(searchTerm);

                card.hidden = !matches;
                hasVisibleAsset = hasVisibleAsset || matches;
            });

            group.hidden = !hasVisibleAsset;
        });
    });
}

