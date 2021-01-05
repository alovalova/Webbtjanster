
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
            url: "http://localhost:5000",
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
            error: function() {
                $("#formFailed").text("Kontrollera postnummer!");
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

            
            console.log(response);
            // Diven som innehåller info om hur långt man hinner resa osv
            $(".travelResult").append("<div class=story-box> BLABLA INFO OM RESAN" + response.arrivalCities[0] +"</div>");
            // Skriver ut första diven som resan utgår ifrån
            $(".travelResult").append("<div class=cities>" + response.arrivalCities[0] +"</div>");
            // Skapar diven som ska röra sig mellan de olika städerna
            $(".cities").append("<div class=airplane></div>");
            // För varje stad vi får som svar körs denna loopen och sköter animationen som rör sig mellan städerna
            for (let i=1; i<response.arrivalCities.length; i++) {
                var airplane = $(".airplane");
                airplane.animate({left: "+=0px"}, 1000);
                airplane.animate({left: "+=172px"}, 1500,
                function() {
                    $(".travelResult").append("<div class=cities>" + response.arrivalCities[i] +"</div>");
                })
                airplane.animate({left: "+=0"}, 500);
            
            }
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