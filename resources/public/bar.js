var bar = function(vals) {

var i = 0;

var next = function() {
    var r = hochzeit.bar.getVals(vals, i);
    i++;
    return r;
};

var w = 20, h = 80;
var t = 1297110663; // start time (seconds since epoch)
var v = 70; // start value (subscribers)

// setInterval(function() {
//     data.shift();
//     data.push(next(vals, i));
//     redraw();
// }, 1500);

// var oldFn =
// 	function(d) {
// 	    return hochzeit.bar.fy(d3, h)(d.value)
// 	}
// console.log("oldFn: ", oldFn);

hochzeit.bar.chart(
    d3, vals, next, w, h, i,
    function(d) {
	return h - hochzeit.bar.fy(d3, h)(d.value) - .5;
    }
);

}
