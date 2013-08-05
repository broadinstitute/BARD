<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="assaycompare"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Comparison Report</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span6">
            <h1>Assay Comparison Report</h1>
        </div>

        <div class="span3">

        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span6">
            <br/>
            <br/>
            <g:formRemote url="[controller: 'assayDefinition', action: 'generateAssayComparisonReport']"
                          name="compareForm"
                          update="[success: 'compare', failure: 'errorMsgId']"
                          onSuccess="handleSuccess()"
                          onFailure="handleError()">
                Enter ADIDs to compare : <g:textField name="assayOneId" class="adid" title="Enter an existing ADID"
                                                      required=""/>
                <g:textField name="assayTwoId" class="adid" title="Enter an existing ADID" required=""/>
                <input type="submit" value="compare"/>

                <div id="errorMsgId"></div>

            </g:formRemote>
        </div>

        <div class="span3">
        </div>
    </div>
</div>
<hr/>

<div id="compare">

</div>

</body>
</html>