var bar = function(vals) {

console.log("vals: ", vals);
var valsLen = vals.length;

var i = 0;

var next = function(){
    return vals[i++];
};

var w = 20, h = 80;

var x = d3.scale.linear()
    .domain([0, 1])
    .range([0, w]);

var y = d3.scale.linear()
    .domain([0, 100])
    .rangeRound([0, h]);

var t = 1297110663, // start time (seconds since epoch)
v = 70, // start value (subscribers)
data = d3.range(valsLen).map(next); // starting dataset

// setInterval(function() {
//     data.shift();
//     data.push(next(vals, i));
//     redraw();
// }, 1500);

var chart = d3.select("body").append("svg")
    .attr("class", "chart")
    .attr("width", w * data.length - 1)
    .attr("height", h);

chart.selectAll("rect")
    .data(data)
    .enter().append("rect")
    .attr("x", function(d, i) { return x(i) - .5; })
    .attr("y", function(d) { return h - y(d.value) - .5; })
    .attr("width", w)
    .attr("height", function(d) { return y(d.value); });

chart.append("line")
    .attr("x1", 0)
    .attr("x2", w * data.length)
    .attr("y1", h - .5)
    .attr("y2", h - .5)
    .style("stroke", "#000");

}
