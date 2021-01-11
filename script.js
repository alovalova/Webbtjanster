
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
    form_data.departureZip = $("input[name=departureZip]").val();
    form_data.departureAddress = $("input[name=departureAddress]").val();
    form_data.arrivalCountry = $("select[name=arrivalCountry]").val();
    form_data.arrivalZip = $("input[name=arrivalZip]").val();
    form_data.arrivalAddress = $("input[name=arrivalAddress]").val();
    form_data.departureDate = $("input[name=departureDate]").val();

    /* Validering för formuläret. Om ett fält är tomt, skriv ut felmeddelande 
        och sätt formValidate till false så att formuläret inte skickas iväg */
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
    formValidate = false;
    }
    else {
    $("#arrivalAddress").text("");
    }
    if (form_data.arrivalZip === "") {
    $("#arrivalZip").text("Du måste ange ett postnummer!");
    formValidate = false;
    }
    else {
    $("#arrivalZip").text("");
    }

    // Hämtar ut dagens datum YYYY-MM-DD och gör om till en sträng
    let date = new Date().toISOString().substring(0, 10);
    // Departure date som väljs av användaren
    let d1 = document.querySelector("#departureDate").value;
    // Dagens datum
    let d2 = date;
    // Om valt datum är tidigare än dagens datum genereras ett felmeddelande
    if (d1 < d2) {
        $("#formFailed").text("Du kan inte resa bakåt i tiden..");
        $("#formFailed").show();
        formValidate = false;
    }

    // Om formuläret validerar görs anropet till APIet 
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
            statusCode: {
                200: function() {
                    console.log("Success 200");
                    $("#formSent").text("Paket skickat!");
                    $("#formSent").show();
                    // Visas i tre sekunder, bekräftelse på att formuläret har skickats
                    setTimeout(function () {
                    $("#formSent").fadeOut("fast");
                    }, 2000); // Tid i millisekunder
                    // Visar diven som håller svaret och animationen
                    $(".travelResult").show(); 
                },
                // Om indatan är felaktig till post-apiet
                400: function() {
                    console.log("Invalid Input");
                    $('#formFailed').text("Kontrollera postnummer");
                    $("#formFailed").show();
                    $(".loader").hide();
                },
                // Om Inga flyg hittas
                404: function() {
                    console.log("Flights not found")
                    $('#formFailed').text("Inga flyg hittades, försök igen!");
                    $("#formFailed").show();
                    $(".loader").hide();
                },
            }
        })

        /* Denna funktion anropas när informationen hämtats. Inladdad
            information går att nå via variabeln "response" */
        .done(function(response) {
            // Döjer formuläret för att ge plats åt svaret
            $("#parcelForm").hide();
            // Knapp för att visa/dölja formuläret
            $("#showForm").show();
            $("#showForm").text("Visa formulär");

            // Loader döljs när svaret har kommit
            $(".loader").hide();

            /* Tömmer divarna som ska presentera svaret ifall där redan
                ligger text i när nytt svar ska presenteras */
            if ($(".story-box").text() != "") {
                $(".story-box").empty();
                $(".animation-box").empty();
            }

            // Visar upp hela svaret vi får i consolen
            console.log(response);
            
            /* Tar bort regionen som skrivs ut innan staden och lägger till
                i en lista som sedan kan itereras över.
                Svaret vi får från början ser ut såhär: "Europe/Madrid" 
            */
            var arrCities = []
            for (let i=0; i<response.arrivalCities.length; i++) {
                let splitCities = response.arrivalCities[i].split("/");
                for (let x=0; x<splitCities.length; x++) {
                    if (x % 2 != 0) {
                    arrCities.push(splitCities[x]);
                    }
                }
            }

            // Statiska delen av svaret då utgångsstad alltid är samma
            $(".story-box").append(
                "<p> Resan börjar i " +
                response.departureCities[0].slice(7,) + ".</p>");

            // Gör om datumsträngen till ett format enlig "YYYY-MM-DD"
            var deliveryDate = response.packageDeliveryDate.slice(0, 4) + 
            "-" + response.packageDeliveryDate.slice(4,6) + "-" + 
            response.packageDeliveryDate.slice(6,9);

            // Den dynamiska delen av svaret
            for (let i=0; i<arrCities.length; i++) {
                $(".story-box").append(
                    "<p>Sedan åker du till " + arrCities[i] + ".</p>");
            }
            $(".story-box").append(
                "<p>Paketet är framme vid sin slutdestination klockan " 
                + response.packageDeliveryTime + " den " +
                deliveryDate +  ". </p>");

            // Skriver ut första staden som resan utgår ifrån
            $(".animation-box").append("<div class=cities>" + 
            response.departureCities[0].slice(7,) + "</div>");

            // Skapar diven som ska röra sig mellan de olika städerna
            $(".cities").append("<div class=airplane></div>");

            /* För varje stad vi får som svar körs denna loopen och sköter
                animationen som rör sig mellan städerna */ 
            var airplane = $(".airplane");
            for (let i=0; i<arrCities.length; i++) {
                airplane.animate({marginTop: "+=0px"}, 1000);
                airplane.animate({left: "+=78px"}, 1500,
                function() {
                    $(".animation-box").append("<div class=cities></<div>");
                    // Lägger till paragrafen i sista "cities" diven
                    $(".cities").last().append("<p>" +  
                    arrCities[i] + "</p>");
                });
                airplane.animate({left: "+=0"}, 500);
            }
        });
    }
}

// Detta körs när hela sidan har laddats in
$(document).ready(function () {
    // Döljer diven som håller svaret och animationen som default
    $(".travelResult").hide(); 

    // Döljer knappen för att visa/dölja formuläret
    $("#showForm").hide();

    // Döljer loadern från början, visas när formuläret skickas
    $(".loader").hide();

    /* Ändrar texten i knappen för att visa/dölja formuläret beroende på om
        formuläret just nu är synligt eller inte */
    $("#showForm").click(function() {
        if($("#parcelForm").is(":visible")){
            $("#showForm").text("Visa Formulär");
        }
        else if ($("#parcelForm").is(":hidden")){
            $("#showForm").text("Dölj Formulär");
        }
        $("#parcelForm").toggle("slow");

    });

    /* När man klickar på "Skicka" i formuläret så körs funktionen 
        getDestinations */
    $("#submitForm").click(getDestinations);

    // Hämtar ut dagens datum YYYY-MM-DD och gör om till en sträng
    let date = new Date().toISOString().substring(0, 10);
    // Fyller i datumfältet automatiskt med dagens datum
    document.querySelector("#departureDate").value = date;
});
