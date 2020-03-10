var color = ["#BCE784", "#5DD39E", "#348AA7", "#525174", "#513B56"];
var months = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'];
var state = "讀表值(Wh)";
var readingData, selectedMonthData, selectedDayData;
var selectedYear, selectedMonth, selectedDay;
var number;
$(document).ready(function(){
	$('input[type=radio][name=optradio]').change(function() {
        if (this.value == 'value') {
            state = "讀表值(Wh)";
        }
        else if (this.value == 'cost') {
			state = "電費(TWD)";
        }
		if (selectedDayData) drawChart3();
		else if(selectedMonthData) drawChart2();
		else drawChart1();
    });
	
    $.ajax({
        url: "/checkAuthState",
        dataType: "text",
        success: function(response)  {
            if(response == "null") $("#authBtn").show();
            else {
				number = response;
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
            $("#updated").empty().append("&nbsp;&nbsp;電號：" + number+"&nbsp;&nbsp;最後更新：" + response[0]).show();
            $("#power").empty().append(response[1]);
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
            $("#updated").empty().append("電號：" + number+"&nbsp;&nbsp;最後更新：" + response[0]).show();
            $("#power").empty().append(response[1]);
            readingData = response;
            drawChart1();
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
	selectedMonthData = null;;
	$("#left").hide();
	$("#chart1").empty().append("<canvas id='canvas1'></canvas>");
	$("#chart1").show();
	$("#chart2").hide();
	$("#chart3").hide();
	var datasets = [];
	color.sort(randomsort);
	$("#analysisResults").empty();
	for (var i = 2; i<readingData.length; i++){
		var data = new Object();
		data.label = readingData[i][0]+"年";
		data.borderColor = color[i];
		data.backgroundColor = color[i];
		data.pointRadius = 6,
		data.pointHoverRadius = 10;
		data.pointStyle = 'rect';
		data.fill = false;
		if(state == "讀表值(Wh)")
			data.data = readingData[i].slice(1, 13);
		else 
			data.data = readingData[i].slice(13, 25);
        datasets.push(data);
		analyses(data.label, data.data);
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
			events: ['mousemove', 'click'],
			onClick: function (event, chartElement){
			    if(chartElement[0]) {
					selectedYear = readingData[chartElement[0]._datasetIndex+2][0];
					selectedMonth = chartElement[0]._index + 1;
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
			},
			onHover: function(event, chartElement){
				event.target.style.cursor = chartElement[0] ? 'pointer' : 'default';
			}
		}
	});
}

function drawChart2(){
	selectedDayData = null;
	$("#left").hide();
	$("#chart2").empty().append("<canvas id='canvas2'></canvas>");
	$("#chart2").show();
	$("#chart1").hide();
	$("#chart3").hide();
	var datasets = [];
	var num = Math.round(selectedMonthData.length/2 - 1);
	color.sort(randomsort);
	
	var data = new Object();
	data.label = selectedYear + "年" + selectedMonth + "月";
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
	
	$("#analysisResults").empty();
	analyses(data.label, data.data);
	
	var days = new Array(num);
	days = initDays(days, 1);
	
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
			events: ['mousemove', 'click'],
			onClick: function (event, chartElement){
			    if(chartElement[0]) {
					selectedDay = chartElement[0]._index + 1;
					$.ajax({
						url: "/getSelectedDayData",
						data: {
							year: selectedYear,	//year
							month: selectedMonth,	//month
							day: selectedDay	//day
						},
						dataType: "json",
						success: function(response)  {
							selectedDayData = response;
							drawChart3();
						},
						error: function(xhr, ajaxOptions, thrownError) {
							console.log("error: " + xhr.status + "\n" + thrownError);
						}
					});
                }			    
			},
			onHover: function(event, chartElement){
				event.target.style.cursor = chartElement[0] ? 'pointer' : 'default';
			}
		}
	});
	if (selectedMonthData) $("#left").show();	
}

function drawChart3(){
	$("#left").hide();
	$("#chart3").empty().append("<canvas id='canvas3'></canvas>");
	$("#chart3").show();
	$("#chart1").hide();
	$("#chart2").hide();
	var datasets = [];
	color.sort(randomsort);
	
	var data = new Object();
	data.label = selectedYear + "年" + selectedMonth + "月" + selectedDay + "日";
	data.borderColor = color[0];
	data.backgroundColor = color[0];
	data.pointRadius = 5,
	data.pointHoverRadius = 9;
	data.pointStyle = 'rectRot';
	data.fill = false;
	if(state == "讀表值(Wh)")
		data.data = selectedDayData.slice(0, 24);
	else 
		data.data = selectedDayData.slice(24, 48);
    datasets.push(data);
	
	$("#analysisResults").empty();
	analyses(data.label, data.data);
	
	var hours = new Array(24);
	hours = initDays(hours, 0);
	
    var lineChartData = {
        labels: hours,
        datasets: datasets
    };
	
	myLine = Chart.Line($("#canvas3"), {
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
						labelString: '時'
					}
				}],
				yAxes: [{
					display: true,
					scaleLabel: {
						display: true,
						labelString: state
					}
				}]
			}
		}
	});
	if (selectedDayData) $("#left").show();	
	
}

function back(){
	if (selectedDayData) drawChart2();
	else if(selectedMonthData) drawChart1();
}

function randomsort(a, b) {
	return Math.random()>.5 ? -1 : 1;
	//用Math.random()函式生成0~1之間的隨機數與0.5比較，返回-1或1
}

function initDays(days, initValue){
	for(var i=0; i<days.length; i++){
		days[i] = i+initValue;
	}
	return days;
}

function analyses(title, array){
	$("#analysisResults").append("<b>"+title+"</b>: 最大<span class='high'>"+Math.max(...array)+"</span>, 最小<span class='low'>"+Math.min(...array)+"</span>, 平均"+average(array, 2)+", 標準差"+standardDeviation(array)+"<br>");
}

function standardDeviation(array){
	avg = average(array,2);
    var squareDiffs = array.map(function(value){
        var diff = value - avg;
        var sqrDiff = diff * diff;
        return sqrDiff;
    });

    sum = 0;
    for(var i=0; i<squareDiffs.length; i++){
        sum += parseFloat(squareDiffs[i]);
    }
	avgSquareDiff = average(squareDiffs, null);
	return Math.sqrt(avgSquareDiff).toFixed(2);
}
	
function average(array, num){
    var sum = 0;
    var count = 0;
    for(var i=0; i<array.length; i++){
        if(!isNaN(array[i])) {
            sum += parseFloat(array[i]);
            count+=1;
        }
    }
    return (sum/count).toFixed(num);
}

