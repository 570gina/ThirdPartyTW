var intervalData =  "";
var state = "0";
$(document).ready(function(){
    $('input[type=radio][name=optradio]').change(function() {
        state = this.value;
        if (this.value == "1") {
            $('#selectYear').prop('disabled', false);
        }else{
            $('#selectYear').prop('disabled', true);
        }
        drawChart();
    });

    $.ajax({
        url: "/checkAuthState",
        dataType: "text",
        success: function(response)  {
            if(response == "false") $("#authBtn").show();
            else {
              //  getNumber();
                getChartData();
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            console.log("error: " + xhr.status + "\n" + thrownError);
        }
    });
});
function getChartData() {
    $("#loadingGif").show();
    $.ajax({
        url: "/getChartData",
        dataType: "json",
        success: function(response)  {
            $("#updateBtn").show();
            $("#loadingGif").hide();
            $("#updated").empty().append("&nbsp;&nbsp;最後更新：" + response.updated);
            $("#title").empty().append(response.title);
            intervalData = JSON.parse(response.intervalData).intervalDayList
            drawChart();
            $("#chart").show();
        },
        error: function(xhr, ajaxOptions, thrownError) {
            $("#loadingGif").hide();
            console.log("error: " + xhr.status + "\n" + thrownError);
        }
    });
}

function drawChart() {
    var color = ["#ff6373", "#8BCDFF", "#FFEC3C", "#2C7AFF", "#ff8ef1", "#44AE52", "#8161FF", "#FFAF46", "#8E8E8E"];
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    var tempArr = [];
    var datasets = [];
    var mon, day;
    var sum;
    if (state == "0") {
        sum = 0;
        for (var i = 1; i < intervalData.length; i++) {
            mon = parseInt(intervalData[i].month);
            sum = add(sum, intervalData[i].data);
            tempArr[mon-1] = sum;
        }
        var result = analyses(tempArr);
        $("#high").empty().append("最高用電：" + result[0]);
        $("#low").empty().append("最低用電：" + result[1]);
        $("#avg").empty().append("平均：" + result[2]);
        $("#sd").empty().append("標準差：" + result[3]);
        var note = getYearNote(tempArr);
        $("#note").empty().append(note);

        var data = new Object();
        data.label = "2018";
        data.borderColor = color[0];
        data.backgroundColor = color[0];
        data.pointHoverRadius = 6;
        data.fill = false;
        data.data = tempArr;
        datasets.push(data);
        tempArr = [];
        $("#chartArea").empty().append("<canvas id='canvas'></canvas>");
        var lineChartData = {
            labels: months,
            datasets: datasets
        };
        myLine = Chart.Line($("#canvas"), {
            data: lineChartData,
            options: {
                responsive: true,
                hoverMode: 'index',
                stacked: false,
                title: {
                    display: true,
                    text: 'All Data'
                },
                scales: {
                    xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Month'
                        }
                    }],
                    yAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Value'
                        }
                    }]
                }
            }
        });
    }else{
        sum = 0;
        mon = parseInt(intervalData[1].month);
        for (var i = 1; i < intervalData.length; i++) {
            if(mon == parseInt(intervalData[i].month)){
                day = parseInt(intervalData[i].day);
                sum = add(0, intervalData[i].data);
                tempArr[day-1] = sum;
            }else{
                var data = new Object();
                data.label = months[mon-1];
                data.borderColor = color[mon-5];
                data.backgroundColor = color[mon-5];
                data.pointHoverRadius = 6;
                data.fill = false;
                data.data = tempArr;
                datasets.push(data);
                tempArr = [];
                mon = intervalData[i].month;
                day = parseInt(intervalData[i].day);
                sum = add(0, intervalData[i].data);
                tempArr[day-1] = sum;
            }
        }
        var data = new Object();
        data.label = months[mon-1];
        data.borderColor = color[mon-5];
        data.backgroundColor = color[mon-5];
        data.pointHoverRadius = 6;
        data.fill = false;
        data.data = tempArr;
        datasets.push(data);

        $("#chartArea").append("<canvas id='canvas'></canvas>");
        var lineChartData = {
            labels: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'],
            datasets: datasets
        };
        myLine = Chart.Line($("#canvas"), {
            data: lineChartData,
            options: {
                responsive: true,
                hoverMode: 'index',
                stacked: false,
                title: {
                    display: true,
                    text: $('#selectYear option:selected').val() + "年"
                },
                scales: {
                    xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Day'
                        }
                    }],
                    yAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Value'
                        }
                    }]
                }
            }
        });
    }
}
function averageInterval(objArr){
    var sum = 0;
    for(var i=0; i<objArr.length; i++){
        sum += objArr[i].value;
    }
    return (sum/objArr.length).toFixed(2);
}

function add(sum, array){
    for(var i=0; i<array.length; i++){
        sum = sum + array[i];
    }
    return sum;
}

function analyses(arr){
    var h, l, avg, sd, note;
    var sum = 0;
    h = max(arr);
    l = min(arr);
    avg = average(arr,2);

    var squareDiffs = arr.map(function(value){
        var diff = value - avg;
        var sqrDiff = diff * diff;
        return sqrDiff;
    });

    sum = 0;
    for(var i=0; i<squareDiffs.length; i++){
        sum += parseFloat(squareDiffs[i]);
    }
    avgSquareDiff = average(squareDiffs, null);

    var sd = Math.sqrt(avgSquareDiff).toFixed(2);

    return [h, l, avg, sd];
}

function average(arr, num){
    var sum = 0;
    var count = 0;
    for(var i=0; i<arr.length; i++){
        if(!isNaN(arr[i])) {
            sum += parseFloat(arr[i]);
            count+=1;
        }
    }
    return (sum/count).toFixed(num);
}

function max(arr){
    var max = 0;
    for(var i=0; i<arr.length; i++){
        if (!isNaN(arr[i]) && arr[i]>max){
            max = arr[i];
        }
    }
    return max;
}

function min(arr){
    var min = 100000000000;
    for(var i=0; i<arr.length; i++){
        if (!isNaN(arr[i]) && arr[i]<min){
            min = arr[i];
        }
    }
    return min;
}

function getYearNote(arr){
    var nan = 0;
    for(var i=0; i<arr.length; i++){
        if (isNaN(arr[i])){
            nan += 1;
        }
    }
    if (nan<7){
        return "提醒：目前暫無";
    }else{
        return "提醒：目前暫無，需要更多的資料";
    }
}

function updateMyData() {
    $("#loadingGif").show();
    $("#chart").hide();
    $.ajax({
        url: "/updateMydata",
        dataType: "json",
        success: function(response)  {
            $("#loadingGif").hide();
            $("#updateBtn").show();
            $("#updated").empty().append("&nbsp;&nbsp;最後更新：" + response.updated);
            $("#title").empty().append(response.title);
            intervalData = JSON.parse(response.intervalData).intervalDayList
            drawChart();
            $("#chart").show();
        },
        error: function(xhr, ajaxOptions, thrownError) {
            $("#loadingGif").hide();
            console.log("error: " + xhr.status + "\n" + thrownError);
        }
    });
}