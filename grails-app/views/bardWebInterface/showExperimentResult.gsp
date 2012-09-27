<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.springframework.context.annotation.Primary; bard.core.ExperimentValues; bard.core.AssayValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Experiment Result : ${experimentId}</title>
    <r:require modules="experimentData"/>
</head>

<body>
    <div id="experimentalResults" href="${createLink(controller:'bardWebInterface' , action: 'showExperimentResult', id:experimentId)}">

    </div>
</body>
</html>