document.addEventListener("DOMContentLoaded", () => {
    const menuButton = document.getElementById("menuButton");
    const sidebarOverlay = document.getElementById("sidebarOverlay");
    const searchInput = document.getElementById("searchUser");
    const roleFilter = document.getElementById("roleFilter");
    const visibleUserCount = document.getElementById("visibleUserCount");
    const noResults = document.getElementById("noResults");
    const userRows = Array.from(document.querySelectorAll(".user-row"));

    function closeSidebar() {
        document.body.classList.remove("sidebar-open");
    }

    function filterUsers() {
        const searchValue = (searchInput?.value || "").trim().toLowerCase();
        const selectedRole = roleFilter?.value || "ALL";
        let visibleCount = 0;

        userRows.forEach(row => {
            const searchableText = (row.dataset.search || "").toLowerCase();
            const role = row.dataset.role || "";
            const matchesSearch = searchableText.includes(searchValue);
            const matchesRole = selectedRole === "ALL" || role === selectedRole;
            const shouldShow = matchesSearch && matchesRole;

            row.hidden = !shouldShow;

            if (shouldShow) {
                visibleCount += 1;
            }
        });

        if (visibleUserCount) {
            visibleUserCount.textContent = String(visibleCount);
        }

        if (noResults) {
            noResults.hidden = visibleCount > 0 || userRows.length === 0;
        }
    }

    menuButton?.addEventListener("click", () => {
        document.body.classList.toggle("sidebar-open");
    });

    sidebarOverlay?.addEventListener("click", closeSidebar);
    searchInput?.addEventListener("input", filterUsers);
    roleFilter?.addEventListener("change", filterUsers);

    document.querySelectorAll(".delete-form").forEach(form => {
        form.addEventListener("submit", event => {
            const button = form.querySelector(".delete-button");
            const username = button?.dataset.username || "this user";
            const confirmed = window.confirm(
                `Delete ${username}? This action cannot be undone.`
            );

            if (!confirmed) {
                event.preventDefault();
            }
        });
    });

    window.addEventListener("resize", () => {
        if (window.innerWidth > 900) {
            closeSidebar();
        }
    });
});
