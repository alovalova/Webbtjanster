
// Funktionen som skickar iväg formuläret
function getDestinations() {
    // Döljer "formulär skickat"
    $("#formSent").hide();

    // Används för att välja när formuläret ska skickas iväg
    var formValidate = true

    // Skapar ett objekt för att lägga till värdena från formuläret
    var form_data = {}
    form_data.departureCountry = $("select[name=departureCountry]").val();
    form_data.departureAddress = $("input[name=departureAddress]").val();
    form_data.departureZip = $("input[name=departureZip]").val();
    form_data.arrivalCountry = $("select[name=arrivalCountry]").val();
    form_data.arrivalAddress = $("input[name=arrivalAddress]").val();
    form_data.arrivalZip = $("input[name=arrivalZip]").val();
    form_data.departureDate = $("input[name=departureDate]").val();

    /* 
    Validering för formuläret. Om ett fält är tomt, skriv ut felmeddelande och sätt
    formValidate till false så att formuläret inte skickas iväg 
    */
    if (form_data.departureAddress == "") {
    $("#departureAddress").text("Du måste ange en adress!");
    formValidate = false;
    }
    else {
    $("#departureAddress").text("");
    console.log("departureAddress");
    }
    if (form_data.departureZip === "") {
    $("#departureZip").text("Du måste ange ett postnummer!");
    formValidate = false;
    }
    else {
    $("#departureZip").text("");
    }
    if (form_data.arrivalAddress === "") {
    $("#arrivalAddress").text("Du måste ange en adress!");
    formValidate = false
    }
    else {
    $("#arrivalAddress").text("");
    }
    if (form_data.arrivalZip === "") {
    $("#arrivalZip").text("Du måste ange ett postnummer!");
    formValidate = false
    }
    else {
    $("#arrivalZip").text("");
    }

    /* Anrop till APIet för att hämta ut data */
    if (formValidate == true) {
    // Visas när formuläret validerat och skickas
    $("#formSent").text("Formulär skickat!");
    $("#formSent").show();
    // Visas i tre sekunder, bekräftelse på att formuläret har skickats
    setTimeout(function () {
    $("#formSent").fadeOut("fast");
    }, 3000); // Tid i millisekunder
    $("#parcelForm").hide();

    $.ajax({
        method: "GET",
        // Till en route på vår server där vi kommer åt APIet
        url: "http://localhost:5000",
        // Datan från formuläret skickas
        data: form_data,
        // Berättar för APIet att vi vill ha JSON tillbaka
        headers: {"Accept": "application/json"},
        crossOrigin: true
    })

    /* Denna funktion anropas när informationen hämtamts. Inladdad information går att nå via variabeln "response" */
    .done(function(response) {
        console.log("-----Response-----");
        console.log(response)
        console.log(response.packageDeliveryTime);
        console.log(response.arrivalCities);
        for (i=0; i<response.arrivalCities.length; i++) {
        $(".travelResult").append("<p class=travelDestinations>Du hinner resa från/till " + response.arrivalCities[i] + "</p>");
        }
        console.log(response.arrivalTimes);
    });
    } 
    else {
    console.log("Alert");
    //alert("Formuläret skickades inte, vänligen kontrollera angivna fel!");
    }
}

$(document).ready(function () {
    var test_response = {
        packageDeliveryTime: "2021-01-18 18:30",
        arrivalCities: ["Madrid", "Barcelona", "Rome", "Athens"],
        arrivalTimes: ["2021-01-17 15:30", "2021-01-17 19:00", "2021-01-18 08:30", "2021-01-18 16:00"]
    }
    // När man klickar på "Skicka" i formuläret så körs funktionen getDestinations
    $("#submitForm").click(getDestinations);

    $(".test-btn").click(function(){
        // Diven som innehåll info om hur långt man hinner resa osv
        $(".travelResult").append("<div class=story-box> BLABLA INFO OM RESAN" + test_response.arrivalCities[0] +"</div>");
        // Skriver ut första diven som resan utgår ifrån
        $(".travelResult").append("<div class=cities>" + test_response.arrivalCities[0] +"</div>");
        // Skapar diven som ska röra sig mellan de olika städerna
        $(".cities").append("<div class=test-div></div>");
        // För varje stad vi får som svar körs denna loopen och sköter animationen som rör sig mellan städerna
        for (let i=1; i<test_response.arrivalCities.length; i++) {
            var div = $(".test-div");
            div.animate({left: "+=130px"}, 2000,
            function() {
                $(".travelResult").append("<div class=cities>" + test_response.arrivalCities[i] +"</div>");
            })
            div.animate({left: "+=0"}, 500);
            
        }
    });

/*
var test_response = {
    packageDeliveryTime: "2021-01-18 18:30",
    arrivalCities: ["Madrid", "Barcelona", "Rome", "Athens"],
    arrivalTimes: ["2021-01-17 15:30", "2021-01-17 19:00", "2021-01-18 08:30", "2021-01-18 16:00"]
}

$(".travelResult").append("<p class=travelDestinations>Du påbörjar din resa från " + test_response.arrivalCities[0] + "<p>");
$(".travelResult").append("<p class=travelDestinations> och ditt flyg lyfter " + test_response.arrivalTimes[0] + "<p>");
for (i=1; i<test_response.arrivalCities.length; i++) {
    $(".travelResult").append("<p class=travelDestinations>Sedan åker du till " + test_response.arrivalCities[i] + "<p>");
    $(".travelResult").append("<p class=travelDestinations>Ditt flyg landar " + test_response.arrivalTimes[i] + "<p>");
        console.log(test_response.arrivalCities[i]);
}
*/

// Hämtar ut dagens datum YYYY-MM-DD och gör om till en sträng
var date = new Date().toISOString().substring(0, 10);
// Fyller i datumfältet automatiskt med dagens datum
document.querySelector("#departureDate").value = date;
});