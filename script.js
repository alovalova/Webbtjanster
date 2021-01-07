
// Funktionen som skickar iväg formuläret
function getDestinations() {
    // Döljer "formulär skickat"
    $("#formSent").hide();
    // Döljer felmeddelandet när man skickar formuläret på nytt
    $("#formFailed").hide();

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
    /* Om formuläret validerar görs anropet till APIet */
    if (formValidate == true) {
        // Om formuläret validerat visas loadern medans vi väntar på svar
        $(".loader").show();
        $.ajax({
            method: "GET",
            // Till en route på vår server där vi kommer åt APIet
            url: "http://localhost:5000/v1/getDestinations",
            // Datan från formuläret skickas
            data: form_data,
            // Berättar för APIet att vi vill ha JSON tillbaka
            headers: {"Accept": "application/json"},
            crossOrigin: true,
            // Om förfrågan till APIet lyckas
            success: function() {
                $("#formSent").text("Formulär skickat!");
                $("#formSent").show();
                // Visas i tre sekunder, bekräftelse på att formuläret har skickats
                setTimeout(function () {
                $("#formSent").fadeOut("fast");
                }, 2000); // Tid i millisekunder
            },
            // Om förfrågan till APIet misslyckas
            error: function (jqXHR, exception) {
                var msg = '';
                if (jqXHR.status === 0) {
                    msg = 'Not connect.\n Verify Network.';
                } else if (jqXHR.status == 404) {
                    msg = 'Requested page not found. [404]';
                } else if (jqXHR.status == 500) {
                    msg = 'Internal Server Error [500].';
                } else if (exception === 'parsererror') {
                    msg = 'Requested JSON parse failed.';
                } else if (exception === 'timeout') {
                    msg = 'Time out error.';
                } else if (exception === 'abort') {
                    msg = 'Ajax request aborted.';
                } else {
                    msg = 'Uncaught Error.\n' + jqXHR.responseText;
                }
                $('#formFailed').text(msg);
                $("#formFailed").show();
            }
        })

        /* Denna funktion anropas när informationen hämtats. Inladdad
        information går att nå via variabeln "response" */
        .done(function(response) {
            // Döjer formuläret för att ge plats åt svaret
            $("#parcelForm").hide();
            // Knapp för att visa/dölja formuläret
            $("#showForm").show();
            $("#showForm").text("Visa Formulär");

            // Loader döljs när svaret har kommit
            $(".loader").hide();


            /* Tömmer divarna som ska presentera svaret ifall där redan
            ligger text i när nytt svar ska presenteras */
            if ($(".story-box").text() != "") {
                $(".story-box").empty();
                $(".response-animation").empty();
            }
            console.log(response);
            // Statiska delen av svaret då utgångsstad alltid är samma
            $(".story-box").append(
                "<p> Resan börjar i " +
                response.departureCities[0].slice(7,) + " där flyget avgår " +
                "klockan " + response.departureTimes[0] + ".</p>");
            // Den dynamiska delen av svaret
            for (var i=0; i<response.arrivalCities.length; i++) {
                $(".story-box").append(
                    "<p>" +
                    "Flyget därefter går sedan vidare till " +
                    response.arrivalCities[i].slice(7,) + " klockan " +
                    response.arrivalTimes[i] + ".</p>");
            }

            // Skriver ut första staden som resan utgår ifrån
            $(".travelResult").append(
                "<div class=cities>" + response.departureCities[0] +"</div>");
            // Skapar diven som ska röra sig mellan de olika städerna
            $(".cities").append("<div class=airplane></div>");
            // För varje stad vi får som svar körs denna loopen och sköter animationen som rör sig mellan städerna
            for (let x=1; x<response.arrivalCities.length; x++) {
                var airplane = $(".airplane");
                airplane.animate({left: "+=0px"}, 1000);
                airplane.animate({left: "+=172px"}, 1500,
                function() {
                    $(".travelResult").append("<div class=cities>" + response.arrivalCities[x].slice(7,) +"</div>");
                })
                airplane.animate({left: "+=0"}, 500);
            }

            /*
            {packageDeliveryTime: "18:00", departureCities: Array(1), departureTimes: Array(1), arrivalCities: Array(1), arrivalTimes: Array(1), …}
            arrivalCities: ["Europe/Paris"]
            arrivalTimes: ["23:30"]
            departureCities: ["Europe/Madrid"]
            departureTimes: ["22:00"]
            errorMessage: ""
            packageDeliveryTime: "18:00"
            waitingTimes: [15]
            __proto__: Object
            */

        });
    }
}

$(document).ready(function () {
    // Döljer knappen för att visa/dölja formuläret
    $("#showForm").hide();

    // Döljer loadern från början, visas när formuläret skickas
    $(".loader").hide();

    // Ändrar texten i knappen för att visa/dölja formuläret beroende på om
    // formuläret just nu är synligt eller inte
    $("#showForm").click(function() {
        if($("#parcelForm").is(":visible")){
            $("#showForm").text("Visa Formulär");
        }
        else if ($("#parcelForm").is(":hidden")){
            $("#showForm").text("Dölj Formulär");
        }
        $("#parcelForm").toggle("slow");

    });

    // När man klickar på "Skicka" i formuläret så körs funktionen getDestinations
    $("#submitForm").click(getDestinations);

    // Hämtar ut dagens datum YYYY-MM-DD och gör om till en sträng
    var date = new Date().toISOString().substring(0, 10);
    // Fyller i datumfältet automatiskt med dagens datum
    document.querySelector("#departureDate").value = date;
});
