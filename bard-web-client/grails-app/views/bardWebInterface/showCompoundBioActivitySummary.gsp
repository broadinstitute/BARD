<%@ page import="bardqueryapi.ActivityOutcome; bardqueryapi.GroupByTypes; bardqueryapi.FacetFormType" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Compound Bio-Activity Summary: ${tableModel?.additionalProperties?.id}</title>
    <r:require modules="core"/>
    <script src="http://d3js.org/d3.v3.min.js"></script>
    <style>

        /*body {*/
        /*font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;*/
        /*margin: auto;*/
        /*position: relative;*/
        /*width: 960px;*/
        /*}*/

        /*form {*/
        /*position: absolute;*/
        /*right: 10px;*/
        /*top: 10px;*/
        /*}*/

    </style>
</head>

<body>

<div class="row-fluid">

    <div class="span9">
        <div id="sunburstdiv">
            <script>
                <g:makeSunburst />
            </script>
            <script>

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

            </script>
            %{--<script type="text/javascript" src="../../js/sunburstPrep.js"></script>--}%
        </div>
    </div>
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.CompoundBioActivitySummaryForm]"/>

    <h2>Compound Bio Activity Summary <small>(cid: ${tableModel?.additionalProperties?.id})</small></h2>

    <g:form action="showCompoundBioActivitySummary" id="${params.id}">
        <g:hiddenField name="compoundId" id='compoundId' value="${params?.id}"/>
        <div style="text-align: left; vertical-align: middle;">
            <label for="groupByTypeSelect" style="display: inline; vertical-align: middle;">Group-by:</label>
            <g:select id="groupByTypeSelect" name="groupByType"
                      from="${[GroupByTypes.ASSAY, GroupByTypes.PROJECT]}" value="${resourceType}"
                      style="display: inline; vertical-align: middle;"/>
            <g:submitButton class="btn btn-primary" name="groupByTypeButton" value="Group"
                            style="display: inline; vertical-align: middle;"/>
        </div>

    </g:form>

    <div class="span9">
        <div>
            <g:if test="${tableModel.additionalProperties.activityOutcome == ActivityOutcome.ACTIVE}">
                <p class="text-info"><i class="icon-info-sign"></i>Only showing results where the compound is active</p>
            </g:if>
        </div>

        <div id="compoundBioActivitySummaryDiv">
            <g:render template="experimentResultRenderer" model="[tableModel: tableModel, landscapeLayout: false]"/>
        </div>
    </div>
</div>
</body>
</html>