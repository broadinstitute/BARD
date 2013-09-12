<!doctype html>
<html>
	<head>
        <meta name="layout" content="logoSearchCartAndFooter"/>
        <title>BARD : An error has occurred</title>
	</head>
	<body>
        <h1>An error has occurred</h1>

        <p>
            (Error ${errorId}) An internal error has occurred.  Please retry your operation.
        </p>

        <g:if test="${showException}">
		    <g:renderException exception="${exception}" />
        </g:if>
	</body>
</html>