<!DOCTYPE html>
<html>
<head>
    <r:require modules="core"/>

    <r:script>

        $(document).ready(function () {
            <%-- Sets the event-handler for the submit button in the form:
            1. Get the search-type value from the radio buttons.
            2. Update the search type in the parent window
            3. Submit the parent's form for searching
            4. Close (send-to-back) the MarvinSketch modal window --%>
        parent.$('#searchButton').click(function () {
          var structureSearchTypeSelected = parent.$('input:radio[name=structureSearchType]:checked').val()

            var marvinSketch = $('#MarvinSketch')[0];
            var smiles = marvinSketch.getMol('smiles')

             //construct the query into a form that we want
            var constructedSearch =structureSearchTypeSelected + ":" + smiles
            parent.$('#searchString').attr('value', constructedSearch);
            parent.$('#aidForm').submit();

//           parent.$('#modalDiv').modal("hide");
//           parent.$('#modalDiv').dialog("close");
        });

        });
    </r:script>
    <r:layoutResources/>
    <r:require modules="bootstrap"/>

</head>

<body>
    <%-- Sets up the MarvinSketch applet. Additional MarvinSketch param could be set here --%>
    <script type="text/javascript" SRC="${request.contextPath}/marvin/marvin.js"></script>
    <script type="text/javascript">
        msketch_name = "MarvinSketch";
        msketch_begin("${request.contextPath}/marvin", 540, 480);
        msketch_end();
    </script>

<r:layoutResources/>
<r:require modules="bootstrap"/>

</body>
</html>