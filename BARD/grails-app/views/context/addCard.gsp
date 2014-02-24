<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Create Card</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <h2>Add a Card</h2>
    </div>
</div>

<g:form class="form-horizontal" action="createCard" controller="context">
    <g:hiddenField name="ownerId" value="${ownerId}"/>
    <g:hiddenField name="contextClass" value="${contextClass}"/>
    <g:hiddenField name="cardSection" value="${cardSection}"/>

    <div class="control-group">
        <label class="control-label" for="cardName">Name:</label>

        <div class="controls">
            <g:textField id="cardName" name="cardName" required="" class="span11" autofocus=""/>
        </div>
    </div>

    <div class="control-group">
        <div class="controls">
            <g:link controller="${ownerController}" action="show"
                    id="${ownerId}"
                    class="btn">Cancel</g:link>
            <button type="submit" class="btn btn-primary">Save</button>
        </div>
    </div>
</g:form>

</body>
</html>