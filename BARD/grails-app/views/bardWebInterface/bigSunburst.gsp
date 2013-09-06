<!doctype html>
<!--[if IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <title>Linked Hierarchies</title>
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
        linkedVisualizationModule(${cid});
    });


</script>


<style>
/*The following styles are used by the 'still loading' spinner, and therefore we want */
/*to define the styles here instead of in a remote stylesheet                         */
#still_loading {
    position: relative;
    background: #ffffff;
    width: 40%;
    height: 60%;
    vertical-align: middle;
    text-align: center;
    border: 3px solid blue;
    font-family: arial;
    font-size: 18pt;
    top: 100px;
    z-index: 100;
    padding: 25px;
    margin: 40px;
}
#still_loading_image {
    background: #ffffff;
    top: 40px;
    vertical-align: middle;
    text-align: center;
    z-index: 101;
    border: 25px;
}

</style>
<div class='stillLoadingSpinner' id="still_loading">
    Still loading data. Please wait.
    <g:img  id="still_loading_image" dir="images" file="loading_icon.gif" alt="please wait while data load" height="50" width="50"/>
</div>




<div id = "graphs">

    <div id = "pieCharts" class="pieCharts">



        <div id = "a0"  class = "pieChartContainer" style="left: 0.5%; top: 10px; width: 21%;  height: 300px;">
            <div id="a0-chart" class="pieChart">
                <span class="graphTitle">Biological process</span>
                <a class="reset" href="javascript:sharedStructures.resetBiologicalProcessPieChart();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a1"  class = "pieChartContainer" style="left: 22.5%; top: 10px; width: 21%; height: 300px;">
            <div id="a1-chart" class="pieChart">
                <span class="graphTitle">Assay format</span>
                <a class="reset" href="javascript:sharedStructures.resetAssayFormatPieChart();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a2"  class = "pieChartContainer" style="left: 44.5%; top: 10px;  width: 21%; height: 300px;">
            <div id="a2-chart" class="pieChart">
                <span class="graphTitle">Protein class</span>
                <a class="reset" href="javascript:sharedStructures.resetAssayIdDimensionPieChart();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>

        <div id = "a3"  class = "pieChartContainer" style="left: 66.5%; top: 10px; width: 21%; height: 300px;">
            <div id="a3-chart" class="pieChart">
                <span class="graphTitle">Assay type</span>
                <a class="reset" href="javascript:sharedStructures.resetAssayTypePieChart();" style="display: none;">reset</a>
                <span class="reset" style="display: none;"></span>
                <div class = "clearfix"></div>
            </div>

        </div>
        <div id = "a4"  class = "resetDrillButtons" style="left: 88.5%; top: 10px; width: 11.5%; height: 300px;">
        </div>


    </div>



</div>


<table id="data-table" class="dc-data-table bordered"  style="position:absolute; left: 0px; top: 300px;">
    <thead>
    <tr >
        <th style='left: 0%; width: 21.5%;' class="columnsAssociatedWithPies">Biological process</th>
        <th style='left: 22.5%; width: 21%;' class="columnsAssociatedWithPies">Assay format</th>
        <th style='left: 44.5%; width: 21%;' class="columnsAssociatedWithPies">Protein class</th>
        <th style='left: 66.5%; width: 21%;' class="columnsAssociatedWithPies">Assay type</th>
        <th style='left: 88.5%; width: 11.5%;' >ADID</th>
    </tr>
    </thead>
</table>

<div id="widthTest" class="legendLine"></div>


<div id="suburst_container" class="container-fluid" style="position:absolute; left: 10px; top: 1000px;">
    <div id="subcontainer" class="row-fluid">
        <div class="span6">





        </div>

        <div class="span6" style="text-align: center; vertical-align: bottom;">


        </div>

    </div>



<script>

    var minimumValue=0;
    var maximumValue=1;

    var continuousColorScale = d3.scale.linear()
            .domain([0, 1])
            .interpolate(d3.interpolateRgb)
            .range(["#deffd9", "#74c476"]);

</script>


<div class="row-fluid">
    <div class="span9 pull-left">

        <div id="sunburstdiv">

        </div>

    </div>

    <div class="span3" style="height: 600px;">

        <div style="vertical-align: top;">
            <div  style="opacity: 0" id="sunburstContractor" class ='contractButton' ></div>
        </div>



        <div style="float:right;padding-top:200px;">
            <div id="legendGoesHere"></div>

        </div>

        <div style="text-align: center; vertical-align: bottom;">

            <select id="coloringOptions" style="visibility: hidden">
                <option value="1"
                >Color by activity</option>
                <option value="2"
                >Split classes by activity</option>
                <option value="3" >Color by class</option>
            </select>
            <div  style="padding-top: 320px;"></div>
            <select id="activity" style="visibility: hidden">
                <option value="1" >Active only</option>
                <option value="2" >Inactive only</option>
                <option value="3"
                        selected>Active and Inactive</option>
            </select>

        </div>

    </div>

</div>
</div>

</body>
</html>