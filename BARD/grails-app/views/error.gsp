<!DOCTYPE html>
<html>
<head>
    <%-- Sitemesh does not appear to get applied to this page.  It's unclear to me why, but quick googling suggests other people have this problem.
    It's a little unclear to me whether its simply an unresolved issue or whether it has been fixed.   This issue appears to still be
    open: http://jira.grails.org/browse/GRAILS-1844 --%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>BARD : An error has occurred</title>

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
    <r:require modules="core,bootstrap"/>
    <r:layoutResources/>
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">
            <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
                <img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/>
            </a>
        </div>
        <div class="span6">
        </div>
    </div>

    <div class="row-fluid">
        <div class="span12">
            <h1>An error has occurred</h1>

            <p>
                An internal error has occurred.   Details of the error have been logged and the BARD team will investigate.   Please retry your operation.
            </p>

            <p>
                (Error ID: ${errorId})
            </p>

            <g:if test="${showException}">
                <g:renderException exception="${exception}" />
            </g:if>

            <%--
            <p>
                If this error is reoccurring, please let us know what you were doing at the time of the error.
            </p>

            <g:form controller="errors" action="submitFeedback" method="POST">
                <input type="hidden" value="${errorId}" name="errorId">
                <input type="text" name="" value="">
                <input type="text" name="" value="">
                <textarea>
                </textarea>
                <g:submitButton name="Submit Feedback"></g:submitButton>
            </g:form>

            --%>
        </div>
    </div>
</div>
</body>
</html>