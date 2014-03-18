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
            %{--buttons are wired via JS--}%
            <input type="submit" class="btn btn-primary" value="Edit Element" id="btnEditElement">
            <input type="submit" class="btn btn-primary" value="Edit Hierarchy" id="btnEditHierarchy">
        </div>
    </div>
</div>
</body>
</html>
