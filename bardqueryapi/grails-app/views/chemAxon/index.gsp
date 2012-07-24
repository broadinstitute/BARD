<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 6/8/12
  Time: 3:46 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

<body>
<div>
    <g:form name="structure" action="index">
        <g:textArea name="smiles" rows="4" cols="40"/>
        <br/>
        <g:submitButton name="submitSmiles" value="Submit"/>
    </g:form>
</div>

<div style="background: #808080">
    %{--<img src="${createLink(controller: 'chemAxon', action: 'generateStructureImage')}" alt="Structure"/>--}%
    <img src="${createLink(controller: 'chemAxon', action: 'generateStructureImage')}" alt="Structure"/>
</div>

<div>
    <script type="text/javascript" SRC="${request.contextPath}/marvin/marvin.js"></script>
    <script type="text/javascript">
        msketch_begin("${request.contextPath}/marvin", 540, 480);
        msketch_end();
    </script>
</div>
</body>
</html>