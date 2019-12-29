$(document).ready(function(){
    $("#authBtn").hide();
    $("#updateBtn").hide();

    $.ajax({
        url: "/checkAuthState",
        dataType: "text",
        success: function(response)  {
            if(response == "false") $("#authBtn").show();
            else showMyChart();
        },
        error: function(xhr, ajaxOptions, thrownError) {
            console.log("error: " + xhr.status + "\n" + thrownError);
        }
    });
});
function showMyChart() {
    $("#updateBtn").show();
    $("#loadingGif").show();
    $.ajax({
        url: "/getChartData",
        dataType: "text",
        success: function(response)  {
            $("#loadingGif").hide();
            console.log(response);
        },
        error: function(xhr, ajaxOptions, thrownError) {
            $("#loadingGif").hide();
            console.log("error: " + xhr.status + "\n" + thrownError);
        }
    });
}

