<html>
<head>
    <r:require modules="core,bootstrap,elementSelect"/>
    <meta name="layout" content="basic"/>
    <title>Select an Element From List</title>
</head>

<body>
<div class="container-fluid" style="margin: 5px;">
    <div class="row-fluid" style="margin: 5px;">
        <div id="elementList" name="elementList" class="span12"></div>
    </div>

    <div class="row-fluid" style="margin: 5px;">
        <div class="span4 offset8">
            <input type="submit" class="btn btn-primary" value="Update Element" id="btnUpdateElement">
            <input type="submit" class="btn btn-primary" value="Update Hierarchy" id="btnUpdateHierarchy">
        </div>
    </div>
</div>
</body>
</html>
