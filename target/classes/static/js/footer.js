function updateClock() {
    var now = new Date();
    document.getElementById('localTime').textContent = now.toLocaleTimeString();
    document.getElementById('gmtTime').textContent = now.toUTCString().slice(17, 25);
}

setInterval(updateClock, 1000);
updateClock();
