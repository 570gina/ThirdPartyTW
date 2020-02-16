var color = ["#ff6373", "#8BCDFF", "#FFEC3C", "#2C7AFF", "#ff8ef1", "#44AE52", "#8161FF", "#FFAF46", "#8E8E8E"];
var months = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'];
var state = "讀表值(Wh)";
var readingData;
var selectedMonthData;
var selectedYear, selectedMonth;
$(document).ready(function(){
	$('input[type=radio][name=optradio]').change(function() {
        if (this.value == 'value') {
            state = "讀表值(Wh)";
        }
        else if (this.value == 'cost') {
			state = "電費(TWD)";
        }
		if (selectedMonthData) drawChart2()
		else drawChart1();
    });
	
    $.ajax({
        url: "/checkAuthState",
        dataType: "text",
        success: function(response)  {
            if(response == "false") $("#authBtn").show();
            else {
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
    $("#loading").hide();
    $.ajax({
        url: "/connectMyData",
        dataType: "json",
        success: function(response)  {
            $("#loadingGif").hide();
            $("#updated").empty().append("&nbsp;&nbsp;最後更新：" + response[0]).show();
			readingData = response;
            drawChart1();
            $("#loading").show();
            $("#updateBtn").show();
            $("#chart").show();
            $("#chartInfo").show();
        },
        error: function(xhr, ajaxOptions, thrownError) {
            $("#loadingGif").hide();
            $("#loading").show();
            console.log("error: " + xhr.status + "\n" + thrownError);
        }
    });
}
function updateMyData() {
    $("#loadingGif").show();
    $("#loading").hide();
    $.ajax({
        url: "/updateMyData",
        dataType: "json",
        success: function(response)  {
            $("#loadingGif").hide();
            $("#updated").empty().append("&nbsp;&nbsp;最後更新：" + response[0]);
            drawChart1(response[1]);
            $("#loading").show();
            $("#chart").show();
            $("#chartInfo").show();
            $('input[name="optradio"]')[0].checked = true;
        },
        error: function(xhr, ajaxOptions, thrownError) {
            $("#loadingGif").hide();
            console.log("error: " + xhr.status + "\n" + thrownError);
        }
    });
}

function drawChart1() {
	selectedMonthData = null;
	$("#right").hide();
	$("#left").hide();
	$("#chart1").empty().append("<canvas id='canvas1'></canvas>");
	$("#chart1").show();
	$("#chart2").hide();
	var datasets = [];
	color.sort(randomsort);
	for (var i = 1; i<readingData.length; i++){
		var data = new Object();
		data.label = readingData[i][0];
		data.borderColor = color[i];
		data.backgroundColor = color[i];
		data.pointRadius = 4,
		data.pointHoverRadius = 8;
		data.fill = false;
		if(state == "讀表值(Wh)")
			data.data = readingData[i].slice(1, 13);
		else 
			data.data = readingData[i].slice(13, 25);
        datasets.push(data);
	}
    var lineChartData = {
        labels: months,
        datasets: datasets
    };
	myLine = Chart.Line($("#canvas1"), {
		data: lineChartData,
		options: {
			responsive: true,
			hoverMode: 'index',
			stacked: false,
			title: {
				display: false,
			},
			scales: {
				xAxes: [{
					display: true,
					scaleLabel: {
						display: true,
						labelString: '月份'
					}
				}],
				yAxes: [{
					display: true,
					scaleLabel: {
						display: true,
						labelString: state
					}
				}]
			},
			onClick: function (evt, item){
			    if(item[0]) {
					selectedYear = readingData[item[0]._datasetIndex+1][0];
					selectedMonth = item[0]._index + 1;
					$.ajax({
						url: "/getSelectedMonthData",
						data: {
							year: selectedYear,	//year
							month: selectedMonth	//month
						},
						dataType: "json",
						success: function(response)  {
							selectedMonthData = response;
							drawChart2();
						},
						error: function(xhr, ajaxOptions, thrownError) {
							console.log("error: " + xhr.status + "\n" + thrownError);
						}
					});
                }
			}
		}
	});
	if (selectedMonthData) $("#right").show();
}

function drawChart2(){
	$("#right").hide();
	$("#left").hide();
	$("#chart2").empty().append("<canvas id='canvas2'></canvas>");
	$("#chart2").show();
	$("#chart1").hide();
	var datasets = [];
	var num = Math.round(selectedMonthData.length/2 - 1);
	color.sort(randomsort);
	
	var data = new Object();
	data.label = selectedYear + "/" + selectedMonth;
	data.borderColor = color[0];
	data.backgroundColor = color[0];
	data.pointRadius = 4,
	data.pointHoverRadius = 8;
	data.fill = false;
	if(state == "讀表值(Wh)")
		data.data = selectedMonthData.slice(1, num+1);
	else 
		data.data = selectedMonthData.slice(num+1, num*2+1);
    datasets.push(data);
	var days = new Array(num);
	days = initDays(days);
	
    var lineChartData = {
        labels: days,
        datasets: datasets
    };
	
	myLine = Chart.Line($("#canvas2"), {
		data: lineChartData,
		options: {
			responsive: true,
			hoverMode: 'index',
			stacked: false,
			title: {
				display: false,
			},
			scales: {
				xAxes: [{
					display: true,
					scaleLabel: {
						display: true,
						labelString: '日'
					}
				}],
				yAxes: [{
					display: true,
					scaleLabel: {
						display: true,
						labelString: state
					}
				}]
			},
			onClick: function (evt, item){
			    
			}
		}
	});
	if (selectedMonthData) $("#left").show();
	
}


function randomsort(a, b) {
	return Math.random()>.5 ? -1 : 1;
	//用Math.random()函式生成0~1之間的隨機數與0.5比較，返回-1或1
}

function initDays(days){
	for(var i=0; i<days.length; i++){
		days[i] = i+1;
	}
	return days;
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
