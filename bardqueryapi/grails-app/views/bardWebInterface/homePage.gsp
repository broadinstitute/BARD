<%@ page import="bardqueryapi.SearchType; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core"/>

    <r:script>
        $(document).ready(function () {
            var autoOpts = {
                source:"/bardqueryapi/bardWebInterface/autoCompleteAssayNames",
                minLength:2
            };
            $("#searchString").autocomplete(autoOpts);
            $("#accordion").accordion({ autoHeight:false });

            $('#structureModal').click(
                function() {
                    url = '${request.contextPath}/chemAxon/marvinSketch';
                    $("#modalDiv").dialog("open");
                    $("#modalIFrame").attr('src',url);
                    return false;
            });

            $("#modalDiv").dialog({
                modal: true,
                autoOpen: false,
                height: '560',
                width: '600',
                draggable: true,
                resizeable: true,
                title: "To finish click the 'X' button",
                beforeClose: function() {
                    var marvinSketch = $('#modalIFrame').contents().find('#MarvinSketch')[0];
                    var smiles = marvinSketch.getMol('smiles')
		            $('#searchString').attr('value', smiles);
		            return true;
                }
            });

        });
    </r:script>
    <r:layoutResources/>
    <r:require modules="bootstrap"/>


    <title>BARD Home</title>
</head>

<body>
<div>
    <div>
        <h1 style="text-align: center">BARD</h1>

        <h2 style="text-align: center">BioAssay Research Database</h2> <br/><br/>

        <g:if test="${flash.message}">
            <div class="error">${flash.message}</div>
        </g:if>
        <br/><br/>

        <g:form name="aidForm" controller="bardWebInterface" action="search">

            <div class="content ">
                <table class="skinnyTable">
                    <tr>
                        <td>
                            <g:textField id="searchString" name="searchString" value="${params?.searchString}"
                                         size="50"/>
                        </td>
                        <td>
                            <g:submitButton name="search"
                                            value="Search"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: right;">
                            <span id="structureModal" style="color:blue;text-decoration: underline;">
                                Create a structure for a search
                            </span>
                        </td>
                        <td/>
                    </tr>
                </table>
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
<r:require modules="bootstrap"/>

<div id="modalDiv">
    <iframe name="modalIFrame" id="modalIFrame" width="100%" height="100%" marginWidth="0"
            marginHeight="0" frameBorder="0"
            scrolling="auto">

    </iframe>
</div>


</body>
</html>