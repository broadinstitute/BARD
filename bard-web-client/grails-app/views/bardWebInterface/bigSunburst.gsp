<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
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
    %{--<script src="../js/sunburst/d3.min.js"></script>--}%
    %{--<script src="../js/sunburst/createALegend.js"></script>--}%
    %{--<script src="../js/sunburst/createASunburst.js"></script>--}%
    <style>
    #sunburstdiv {
        font-family: sans-serif;
        font-size: 12px;
        position: relative;
    }

    .toolTextAppearance {
        font: 20px serif;
        font-weight: bold;
        margin: 5px;
        padding: 10px;
        background: #eeeeee;
        border: 1px solid blue;
        -moz-border-radius: 15px;
        border-radius: 15px;
    }

    .legend {
        font: 14px sans-serif;
        font-weight: bold;
    }

    .legendHolder {
        border: 3px solid black;
        font: 12px sans-serif;
        font-weight: bold;
        text-align: center;
        background: #eeeeee;
        width: 160px;
    }

    </style>

    <script>
        window.onload = function () {
            $('#activity').change(function () {
                if (this.value == "1") {
                    location.href = "./bigSunburst?actives=t&inactives=f";
                }
                if (this.value == "2") {
                    location.href = "./bigSunburst?actives=f&inactives=t";
                }
                if (this.value == "3") {
                    location.href = "./bigSunburst?actives=t&inactives=t";
                }

            });
            $('#coloringOptions').change(function () {
                if (this.value == "1") {
                    location.href = "./bigSunburst?colorOption=1";
                }
                if (this.value == "2") {
                    location.href = "./bigSunburst?colorOption=2";
                }
                if (this.value == "3") {
                    location.href = "./bigSunburst?colorOption=3";
                }
            });
        }
    </script>

</head>

<body>
<r:layoutResources/>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span6">

            <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
                <img src="${resource(dir: 'images', file: 'bard_logo_med.png')}" alt="BioAssay Research Database"/>
            </a>

        </div>

        <div class="span6" style="text-align: center; vertical-align: bottom;">
            <br/>
            <h2>Panther class hierarchy</h2>
        </div>

    </div>

    <g:bigSunburstData compoundSummary="${compoundSummary}" cid="${cid}"/>

    <div class="row-fluid">
        <div class="span9 pull-left">
            <g:bigSunburstSection compoundSummary="${compoundSummary}" cid="${cid}"/>
        </div>

        <div class="span3" style="padding-top: 50px;  height: 600px;">
            <div style="float:right;">
               <g:sunburstLegend/>
            </div>

            <div style="text-align: center; vertical-align: bottom;">

                <select id="coloringOptions" style="visibility: hidden">
                    <option value="1"
                            <g:if test="${dropDown2Choice == 1}">selected</g:if>>Color by activity</option>
                    <option value="2"
                            <g:if test="${dropDown2Choice == 2}">selected</g:if>>Split classes by activity</option>
                    <option value="3" <g:if test="${dropDown2Choice == 3}">selected</g:if>>Color by class</option>
                </select>
                <div  style="padding-top: 320px;"></div>
                <select id="activity" style="visibility: hidden">
                        <option value="1" <g:if test="${dropDown1Choice == 1}">selected</g:if>>Active only</option>
                        <option value="2" <g:if test="${dropDown1Choice == 2}">selected</g:if>>Inactive only</option>
                        <option value="3"
                                <g:if test="${dropDown1Choice == 3}">selected</g:if>>Active and Inactive</option>
                </select>

            </div>

        </div>
    </div>
</div>

</body>
</html>