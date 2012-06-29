<%@ page import="bardqueryapi.SearchType; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core"/>

    <r:script>
        $(document).ready(function() {
         $( "#accordion" ).accordion({ autoHeight: false });
        })
    </r:script>
    <r:layoutResources/>
    <title>BARD Home</title>
</head>

<body>
<div>
    <div>
        <h1>BARD</h1>

        <h2>BioAssay Research Database</h2> <br/><br/>
        <g:if test="${flash.message}">
            <div class="error">${flash.message}</div>
        </g:if>
        <br/><br/>

    <g:form name="aidForm" controller="bardWebInterface" action="search">

            <div class="content">
                <g:textField name="searchString" value="${params?.searchString}" size="50"/>
                <label for="searchType">
                    <g:message code="compound.compoundType.label" default=" Search Type"/>
                </label>
                <g:select name="searchType" from="${SearchType?.values()}"
                          value="${params?.searchType}" optionValue="description"/>
                 <br/> <br/>
                <g:submitButton name="search"
                                value="Search"/>
                <g:submitButton name="search"
                                value="I'm Feeling Serendipitous"/>

            </div>
        </g:form>
        <br/>
        <br/>
    </div>

     <div id="accordion">
        <g:render template="assays"/>
        <g:render template="compounds"/>
        <g:render template="experiments"/>
        <g:render template="projects"/>
    </div>    <!-- End accordion -->

</div><!-- End body div -->
<r:layoutResources/>
</body>
</html>