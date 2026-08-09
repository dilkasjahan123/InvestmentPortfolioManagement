// Mobile sidebar navigation controls
document.addEventListener("DOMContentLoaded", () => {
    const menuButton = document.getElementById("menuButton");
    const sidebarOverlay = document.getElementById("sidebarOverlay");
    const navigationLinks = document.querySelectorAll(".side-nav .nav-link");

    // Close sidebar
    function closeSidebar() {
        document.body.classList.remove("sidebar-open");
    }

    // Toggle sidebar on mobile
    menuButton?.addEventListener("click", () => {
        document.body.classList.toggle("sidebar-open");
    });

    // Close sidebar when overlay is clicked
    sidebarOverlay?.addEventListener("click", closeSidebar);

    // Close sidebar after navigation selection
    navigationLinks.forEach(link => {
        link.addEventListener("click", closeSidebar);
    });

    // Reset sidebar when returning to desktop layout
    window.addEventListener("resize", () => {
        if (window.innerWidth > 900) {
            closeSidebar();
        }
    });
});
