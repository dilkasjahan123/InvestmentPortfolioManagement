document.addEventListener("DOMContentLoaded", function () {
    var data = window.performanceReportData;

    if (!data || typeof Chart === "undefined") {
        return;
    }

    var allocationCanvas = document.getElementById("allocationChart");
    var allocationEmpty = document.getElementById("allocationEmpty");

    if (data.allocationValues.length > 0) {
        allocationEmpty.style.display = "none";

        new Chart(allocationCanvas, {
            type: "pie",
            data: {
                labels: data.allocationLabels,
                datasets: [{
                    data: data.allocationValues,
                    backgroundColor: ["#1E3A8A", "#2E8B57", "#FFA500", "#8A2BE2"]
                }]
            }
        });
    } else {
        allocationCanvas.style.display = "none";
    }

    var historyCanvas = document.getElementById("historyChart");
    var historyEmpty = document.getElementById("historyEmpty");

    if (data.historyValues.length > 0) {
        historyEmpty.style.display = "none";

        new Chart(historyCanvas, {
            type: "line",
            data: {
                labels: data.historyLabels,
                datasets: [{
                    label: "Return %",
                    data: data.historyValues,
                    borderColor: "#1E3A8A",
                    backgroundColor: "#DCE6F8",
                    fill: true
                }]
            }
        });
    } else {
        historyCanvas.style.display = "none";
    }
});
