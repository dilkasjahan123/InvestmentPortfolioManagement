document.addEventListener("DOMContentLoaded", () => {
    const data = window.performanceReportData;

    if (!data || typeof Chart === "undefined") {
        return;
    }

    renderAllocationChart(data);
    renderHistoryChart(data);
});

function renderAllocationChart(data) {
    const canvas = document.getElementById("allocationChart");
    const empty = document.getElementById("allocationEmpty");

    if (!canvas || data.allocationValues.length === 0) {
        showEmpty(canvas, empty);
        return;
    }

    empty.hidden = true;

    new Chart(canvas, {
        type: "doughnut",
        data: {
            labels: data.allocationLabels.map(formatAssetType),
            datasets: [{
                data: data.allocationValues,
                backgroundColor: ["#6366f1", "#14b8a6", "#f59e0b", "#ec4899"],
                borderColor: "#ffffff",
                borderWidth: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: "68%",
            plugins: {
                legend: {
                    position: "bottom",
                    labels: {usePointStyle: true, padding: 18}
                },
                tooltip: {
                    callbacks: {
                        label: context =>
                            ` ${context.label}: ${Number(context.raw).toFixed(2)}%`
                    }
                }
            }
        }
    });
}

function renderHistoryChart(data) {
    const canvas = document.getElementById("historyChart");
    const empty = document.getElementById("historyEmpty");

    if (!canvas || data.historyValues.length === 0) {
        showEmpty(canvas, empty);
        return;
    }

    empty.hidden = true;

    const labels = [...data.historyLabels].reverse();
    const values = [...data.historyValues].reverse();

    new Chart(canvas, {
        type: "line",
        data: {
            labels,
            datasets: [{
                label: "Return %",
                data: values,
                borderColor: "#6366f1",
                backgroundColor: "rgba(99, 102, 241, 0.12)",
                pointBackgroundColor: "#ffffff",
                pointBorderColor: "#6366f1",
                pointBorderWidth: 3,
                pointRadius: 5,
                tension: 0.35,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    ticks: {callback: value => `${value}%`},
                    grid: {color: "rgba(148, 163, 184, 0.18)"}
                },
                x: {grid: {display: false}}
            },
            plugins: {
                legend: {display: false},
                tooltip: {
                    callbacks: {
                        label: context =>
                            ` Return: ${Number(context.raw).toFixed(2)}%`
                    }
                }
            }
        }
    });
}

function showEmpty(canvas, empty) {
    if (canvas) {
        canvas.hidden = true;
    }
    if (empty) {
        empty.hidden = false;
    }
}

function formatAssetType(value) {
    return String(value)
        .toLowerCase()
        .replaceAll("_", " ")
        .replace(/\b\w/g, character => character.toUpperCase());
}
