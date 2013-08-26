<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Merge Assays</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span8">
            <g:formRemote url="[controller: 'mergeAssayDefinition', action: 'confirmMerge']"
                          name="mergeAssays"
                          update="[success: 'confirmMerge', failure: 'confirmMerge']">

                <div class="control-group">
                    <label class="control-label"
                           for="sourceAssayIds">Enter the IDs of the Assays to merge (separated by spaces) :</label>

                    <div class="controls">
                        <g:textField id="sourceAssayIds" name="sourceAssayIds" required=""/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="targetAssayId">Enter the ID of the Assay to merge into:</label>

                    <div class="controls">
                        <g:textField id="targetAssayId" name="targetAssayId" required=""/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="assayIdType">Interpret IDs as</label>

                    <div class="controls">
                        <g:select name="assayIdType" id="assayIdType"
                                  from="${AssayIdType.values()}"
                                  value="${AssayIdType.ADID}"
                                  optionValue="name"/>
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

<div id="confirmMerge">

</div>

</body>
</html>