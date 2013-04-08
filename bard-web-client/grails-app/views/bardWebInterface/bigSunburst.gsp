<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <title>Sunburst</title>
    <title>BARD : Compound Bio-Activity Summary</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <script src="http://d3js.org/d3.v3.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script>
        window.onload = function () {
            $('#activity').change(function () {
                if(this.value== "1"){
                    location.href = "./bigSunburst?actives=t&inactives=f";
                }
                if(this.value== "2"){
                    location.href =  "./bigSunburst?actives=f&inactives=t";
                }
                if(this.value== "3"){
                    location.href = "./bigSunburst?actives=t&inactives=t";
                }

            });
            $('#coloringOptions').change(function () {
                location.href = "../bigSunburst";
            });
        }
    </script>

    <r:require modules="core"/>
    <r:layoutResources/>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3 offset9">
            <div class="center-aligned">
                <h1>Compound class summary</h1>
            </div>
        </div>

        <div class="span12">
            <div class="center-aligned">
            <select id="activity">
                <option value="1" <g:if test="${dropDown1Choice==1}">selected</g:if> >Active only</option>
                <option value="2" <g:if test="${dropDown1Choice==2}">selected</g:if> >Inactive only</option>
                <option value="3" <g:if test="${dropDown1Choice==3}">selected</g:if> >Active and Inactive</option>
            </select>

                &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;
                &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;

                <select id="coloringOptions">
                <option value="1">Color by activity</option>
                <option value="2">Color by class</option>
            </select>
            </div>
        </div>
    </div>

    <hr>

    <div class="row-fluid">
        <div>
        <g:bigSunburstSection compoundSummary="${compoundSummary}"/>
        </div>
    </div>
</body>
</html>