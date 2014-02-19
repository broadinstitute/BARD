<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Split Assay Definition</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span8">
            <h3>Split Assay Definition</h3>
            <g:formRemote url="[controller: 'splitAssayDefinition', action: 'selectExperimentsToMove']"
                          name="splitExperiments"
                          update="[success: 'confirmSplit', failure: 'confirmSplit']">

                <div class="control-group">
                    <label class="control-label"
                           for="assayId">Enter the ADID of the Assay to Split:</label>

                    <div class="controls">
                        <g:textField id="assayId" name="assayId" required=""/>
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

<div id="confirmSplit">

</div>

</body>
</html>