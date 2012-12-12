<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Experiment Result : ${experimentId}</title>
    <r:require modules="experimentData, bootstrap, compoundOptions"/>
</head>

<body>
    <div id="experimentalResults" href='${createLink(controller:"bardWebInterface", action: "showExperimentResult", id:experimentId)}?searchString=${searchString}'>

    </div>
</body>
</html>