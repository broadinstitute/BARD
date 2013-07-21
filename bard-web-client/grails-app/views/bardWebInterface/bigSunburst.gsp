<!doctype html>
<!--[if IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <title>Sunburst</title>
    <title>BARD : Compound Bio-Activity Summary</title>
    <r:require modules="sunburst"/>
    <r:layoutResources/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

</head>

<body>
<r:layoutResources/>

<script>

    $(document).ready(function () {
        linkedVisualizationModule.buildLinkedHierarchiesVisualization(${cid});
    });


</script>








<div id = "graphs">

    <div id = "pieCharts" class="pieCharts">

        <div id = "a0"  class = "pieChartContainer" style="left: 10px; top: 10px; width: 260px;  height: 300px;">
            <div id="a0-chart" class="pieChart">
                <span class="graphTitle">Biological process</span>
                <a class="reset" href="javascript:sharedStructures.resetBiologicalProcessPieChart();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a1"  class = "pieChartContainer" style="left: 280px; top: 10px; width: 260px; height: 300px;">
            <div id="a1-chart" class="pieChart">
                <span class="graphTitle">Assay format</span>
                <a class="reset" href="javascript:sharedStructures.resetAssayFormatPieChart();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a2"  class = "pieChartContainer" style="left: 550px; top: 10px;  width: 260px; height: 300px;">
            <div id="a2-chart" class="pieChart">
                <span class="graphTitle">Protein target</span>
                <a class="reset" href="javascript:sharedStructures.resetAssayIdDimensionPieChart();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a3"  class = "pieChartContainer" style="left: 820px; top: 10px; width: 260px; height: 300px;">
            <div id="a3-chart" class="pieChart">
                <span class="graphTitle">Assay type</span>
                <a class="reset" href="javascript:sharedStructures.resetAssayTypePieChart();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>
        <div id = "a4"  class = "resetDrillButtons" style="left: 1090px; top: 10px; width: 260px; height: 300px;">
        </div>

</div>



</div>


<table id="data-table" class="table table-hover dc-data-table"  style="position:absolute; left: 0px; top: 300px;">
    <thead>
    <tr class="header">
        <th style='width: 23%' class="data-table-th">Biological process</th>
        <th style='width: 23%' class="data-table-th">Assay format</th>
        <th style='width: 23%' class="data-table-th">Proteins target</th>
        <th style='width: 23%' class="data-table-th">Assay type</th>
        <th style='width: 8%' class="data-table-th">ID</th>
    </tr>
    </thead>
</table>

<div id="widthTest" class="legendLine"></div>


<script>

    var minimumValue=0;
    var maximumValue=1;

    var continuousColorScale = d3.scale.linear()
            .domain([0, 1])
            .interpolate(d3.interpolateRgb)
            .range(["#deffd9", "#74c476"]);

</script>


<div id="suburst_container" class="container-fluid" style="position:absolute; left: 10px; top: 1000px;">
    <div class="row-fluid">
        <div class="span6">


        </div>

        <div class="span6" style="text-align: center; vertical-align: bottom;">
            %{--<br/>--}%
            %{--<h2>Panther class hierarchy</h2>--}%
        </div>

    </div>

    %{--<g:bigSunburstData compoundSummaryPlusId="${compoundSummaryPlusId}" cid="${cid}"/>--}%

    <div class="row-fluid">
        <div class="span9 pull-left">

            <div id="sunburstdiv">
                %{--// Sunburst goes here!--}%
            </div>

        </div>

        <div class="span3" style="padding-top: 50px;  height: 600px;">
            <div style="float:right;">
                <div id="legendGoesHere"></div>

            </div>

        </div>
    </div>
</div>

</body>
</html>