
var svg = d3.select("div#sunburstdiv").append("svg")
    .attr("width", width)
    .attr("height", height + 10)
    .append("g")
    .attr("transform", "translate(" + width / 2 + "," + (height * .5 + 5) + ")");

var x = d3.scale.linear()
    .range([0, 2 * Math.PI]);

var y = d3.scale.sqrt()
    .range([0, radius]);



var partition = d3.layout.partition()
    .sort(null)
    .value(function(d) { return d.size; });

var arc = d3.svg.arc()
    .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
    .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
    .innerRadius(function(d) { return Math.max(0, y(d.y)); })
    .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });

var path = svg.datum($data[0]).selectAll("path")
    .data(partition.nodes)
    .enter().append("path")
    .attr("display", function(d) { return d.depth ? null : "none"; }) // hide inner ring
    .attr("d", arc)
    .style("stroke", "#fff")
    .style("fill", function(d) { return color((d.children ? d : d.parent).name); })
    .style("fill-rule", "evenodd");

var text = svg.datum($data[0]).selectAll("text").data(partition.nodes);

var textEnter = text.enter().append("svg:text")
    .attr("transform", function(d) {
        return "translate(" + arc.centroid(d) + ")";
    })
    .attr("dy", ".35em")
    .attr("text-anchor", "middle")
    .text(function(d) { return d.name; });

d3.select(self.frameElement).style("height", height + "px");
