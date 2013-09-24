<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,moveExperiments"/>
    <meta name="layout" content="basic"/>
    <title>Merge Assays</title>

</head>

<body>
<div class="container">
    <div class="hero-unit">
        <h2>Move Experiments</h2>
    </div>
    <g:formRemote url="[controller: 'moveExperiments', action: 'confirmMoveExperiments']"
                  name="mergeAssays"
                  update="[success: 'confirmMove', failure: 'confirmMove']">
        <div class="row">
            <div class="span6">
                <h4><div id="selectBoxMessage">Identify experiments to Move</div></h4>
                <g:select name="idType" id="idType"
                          from="${IdType.values()}"
                          value="${IdType.EID}"
                          optionValue="name"/>
            </div>

            <div class="span6">
                <h4><div id="textBoxMessage">Identify Entities to Move</div></h4>
                <g:textField class="input-xxlarge" id="sourceEntityIds" name="sourceEntityIds" required=""/>
            </div>

        </div>
        <br/>

        <div class="row">
            <div class="span6">
                <h4>Assay Definition to move to (ADID):</h4>
            </div>
            <br/>

            <div class="span6">
                <g:textField id="targetAssayId" class="input-mini" name="targetAssayId" required=""/>
            </div>

        </div>
        <br/>
        <br/>

        <div class="row">
            <div class="span4">
                <input type="submit" class="btn btn-primary btn-large">
            </div>

            <div class="span4">

            </div>

            <div class="span4">

            </div>
        </div>

    </g:formRemote>
</div>
<br/>
<hr/>

<div id="confirmMove">

</div>
</body>
</html>