<%@ page import="bardqueryapi.StructureSearchType" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core"/>

    <r:script>

        $(document).ready(function () {
            $('#searchButton').click(function () {
                var structureSearchTypeSelected = $('input:radio[name=structureSearchType]:checked').val()
                parent.$('#hiddenFieldStructureSearchType').attr('value', structureSearchTypeSelected)

                var marvinSketch = $('#MarvinSketch')[0];
                var smiles = marvinSketch.getMol('smiles')
                parent.$('#hiddenFieldSmiles').attr('value', smiles);
                parent.$('#structureSearchForm').submit();

                parent.$('#modalDiv').dialog("close");
            });

            $('#cancelButton').click(function () {
                parent.$('#modalDiv').dialog("close");
            });
        });
    </r:script>
    <r:layoutResources/>
    <r:require modules="bootstrap"/>

</head>

<body>
<div>
    <script type="text/javascript" SRC="${request.contextPath}/marvin/marvin.js"></script>
    <script type="text/javascript">
        msketch_name = "MarvinSketch";
        msketch_begin("${request.contextPath}/marvin", 540, 480);
        msketch_end();
    </script>
</div>

<div>
    <table class="skinnyTable" style="margin-top: 10px; float: right;" align="left">
        <tr>
            <td style="padding-right: 20px;">
                <g:radioGroup name="structureSearchType"
                              values="[StructureSearchType.EXACT_MATCH, StructureSearchType.SUB_STRUCTURE, StructureSearchType.SIMILARITY]"
                              value="${StructureSearchType.SUB_STRUCTURE}"
                              labels="[StructureSearchType.EXACT_MATCH.description, StructureSearchType.SUB_STRUCTURE.description, StructureSearchType.SIMILARITY.description]">
                    <p>${it.radio} ${it.label}</p>
                </g:radioGroup>
            </td>
            <td>
                <g:submitButton name="searchButton" id="searchButton" value="Search"/>
                <g:submitButton name="cancelButton" id="cancelButton" value="Cancel"/>
            </td>
        </tr>
    </table>
</div>

<r:layoutResources/>
<r:require modules="bootstrap"/>

</body>
</html>