<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Move Experiments From Source Assay</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span8">
            <h2>Move Experiments From Source Assay to Target Assay</h2>
            <br/>
            <br/>
            <g:formRemote url="[controller: 'moveExperiments', action: 'selectAssays']"
                          name="mergeAssays"
                          update="[success: 'displayResponseDiv', failure: 'displayResponseDiv']">

                <div class="control-group">
                    <label class="control-label"
                           for="sourceAssayId">Enter the ID of the Source Assay :</label>

                    <div class="controls">
                        <g:textField id="sourceAssayId" name="sourceAssayId" required=""/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="targetAssayId">Enter the ID of the Target Assay:</label>

                    <div class="controls">
                        <g:textField id="targetAssayId" name="targetAssayId" required=""/>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <input type="submit" class="btn btn-primary">
                    </div>
                </div>

            </g:formRemote>
        </div>

        <div class="span2">

        </div>
    </div>
</div>
<br/>

<hr/>

<div id="displayResponseDiv">

</div>

</body>
</html>