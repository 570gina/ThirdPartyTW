$(document).ready(function(){
    $("#authBtn").hide();
    $.ajax({
        url: "/checkAuthState",
        dataType: "text",
        success: function(response)  {
            if(response == "false") {
                $("#authBtn").show();
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            console.log("error: " + xhr.status + "\n" + thrownError);
        }
    });
});

