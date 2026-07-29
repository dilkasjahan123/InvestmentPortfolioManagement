document.addEventListener("DOMContentLoaded", () => {
    const menuButton = document.getElementById("menuButton");
    const sidebarOverlay = document.getElementById("sidebarOverlay");
    const navigationLinks = document.querySelectorAll(".side-nav .nav-link");

    function closeSidebar() {
        document.body.classList.remove("sidebar-open");
    }

    menuButton?.addEventListener("click", () => {
        document.body.classList.toggle("sidebar-open");
    });

    sidebarOverlay?.addEventListener("click", closeSidebar);

    navigationLinks.forEach(link => {
        link.addEventListener("click", closeSidebar);
    });

    window.addEventListener("resize", () => {
        if (window.innerWidth > 900) {
            closeSidebar();
        }
    });
});
