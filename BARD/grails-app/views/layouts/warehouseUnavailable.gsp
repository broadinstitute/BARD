<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.ActivityOutcome; bardqueryapi.NormalizeAxis; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>experimentInfoUnavailable
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix,xeditable,experimentsummary,canEditWidget,richtexteditorForEdit, sectionCounter, card,histogram"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>${message}</title>
</head>

<body>
<div class="row-fluid">
    <div class="span2"></div>

    <div class="span8">
        <h3>Oops, we're sorry</h3>

        <p><b>BARD is unable to display this data due to an error in the REST Query API.  We apologize for the inconvenience.
        An email has been sent to the BARD Development Team, and we will investigate the problem as soon as possible.</b>
        </p>

        <p>
        <ul>In the meantime, you can try:
            <li>Waiting a little bit and then repeating what you were doing, in case the error is only temporary;</li>
            <li>Reporting the problem to the <a href="${grailsApplication.config.bard.users.mailing.list}" target="forum">bard-users mailing list</a> with a description of what you are trying to do.
            This will help us to resolve the problem more quickly.</li>
        </ul>

        <p>Thank you for your patience. </p>

        <p>Sincerely,</p>

        <p>The BARD Development Team</p>


    </div>

    <div class="span2"></div>
</div>
</body>
</html>