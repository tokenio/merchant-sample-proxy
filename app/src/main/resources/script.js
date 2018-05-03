function initiatePayment() {
    // prevent multiple clicking
    unbindClick();

    var XHR = new XMLHttpRequest();

    // Set up our request
    XHR.open('POST', 'http://localhost:3000/transfer', true);

    XHR.setRequestHeader("Content-Type", "application/json; charset=utf-8");

    var data = $.param({
        amount: 4.99,
        currency: 'EUR',
        description: 'Book Purchase'
    });

    // Define what happens on successful data submission
    XHR.addEventListener("load", function (event) {
        window.location.assign(event.target.responseURL);
    });

    // Send the data; HTTP headers are set automatically
    XHR.send(data);
}

function bindClick() {
    // Add click listener
    el.addEventListener('click', initiatePayment);
}

function unbindClick() {
    // Remove click listener
    el.removeEventListener('click', initiatePayment);
}

var el = document.getElementById("tokenPayBtn");
bindClick();
