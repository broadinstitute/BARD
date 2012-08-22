<head>
    <meta name="layout" content="main"/>
    <r:require modules="core"/>
    <r:require modules="bootstrap"/>
    %{--<r:require modules="backbone_grid"/>--}%

    <r:script>
        $(document).ready(function () {
            var autoOpts = {
                source:"/bardwebquery/bardWebInterface/autoCompleteAssayNames",
                minLength:2
            };
            $("#searchString").autocomplete(autoOpts);

            $('#aidForm').submit(function(event){
            var searchType = findSearchType()

            switch(searchType.toUpperCase()){
             case 'REGULAR':
              $.ajax({
                 url : '/bardwebquery/bardWebInterface/searchAssays',
                  data: $("#aidForm").serialize(),
                cache: false,
                success: function (data) {
                     $("#assays").html(data);
                    var assayTotal = 'Assays (' + $("#totalAssays").val() + ')'
                    $("#assaysTab").html(assayTotal);
                }
            });
             $.ajax({
                 url : '/bardwebquery/bardWebInterface/searchCompounds',
                  data: $("#aidForm").serialize(),
                cache: false,
                success: function (data) {
                //alert("Compounds: " + data)
                   $("#compounds").html(data);
                    var compoundTotal = 'Compounds (' + $("#totalCompounds").val() + ')'
                    $("#compoundsTab").html(compoundTotal);
                }
            });

                 $.ajax({
                 url : '/bardwebquery/bardWebInterface/searchProjects',
                  data: $("#aidForm").serialize(),
                cache: false,
                success: function (data) {
                //alert("Compounds: " + data)
                   $("#projects").html(data);
                    var projectsTotal = 'Projects (' + $("#totalProjects").val() + ')'
                    $("#projectsTab").html(projectsTotal);
                }
            });
             break;
             case 'ID':
             //go to the id search resources
             alert("Not yet implemented")
             break;
             case 'STRUCTURE':
                  $.ajax({
                 url : '/bardwebquery/bardWebInterface/searchStructures',
                 data: $("#aidForm").serialize(),
                cache: false,
                success: function (data) {
                    $("#projects").html('');
                    var projectsTotal = 'Projects (0)'
                    $("#projectsTab").html(projectsTotal);

                    $("#assays").html('');
                    var assaysTotal = 'Assays (0)'
                    $("#assaysTab").html(assaysTotal);

                    $("#compounds").html(data);
                    var compoundTotal = 'Compounds (' + $("#totalCompounds").val() + ')'
                    $("#compoundsTab").html(compoundTotal);
                }
            });
             break;


            }
            return false; //do not submit form the normal way, use Ajax instead

    });

            <%-- An even handler to open (bring-to-front) the MarvinSketch modal window. Opens MarvingSketch in a new gsp: marvinSketch.gsp --%>
        $('#structureModal').click(
        function() {
      url = '${request.contextPath}/chemAxon/marvinSketch';
                    $("#modalDiv").dialog("open");
                    $("#modalIFrame").attr('src',url);
                    return false;
            });

    <%-- Define the MarvinSketch modal window (JQuery UI Modal) --%>
        $("#modalDiv").dialog({
      modal: true,
      autoOpen: false,
      height: '660',
      width: '600',
      draggable: true,
      resizeable: true,
      title: "",
      beforeClose: function() {
    <%-- Processing before closing (send-to-back) of the modal window could be done here
FALSE will prevent closing of the modal window. --%>
        return true;
                    }
                });
            });

        function findSearchType(){

           var searchString = $("#searchString").val()
          if(!$.trim(searchString).length) {  //if this is an empty string
             return "Empty"
           }
           var regex = /^\d+(?:, *\d+ *)*$/;
           if(searchString.match(regex)){//this is an id match
            return "ID"
           }
           //we want to find out if this is a Structure search
           var searchStringSplit = searchString.split(":");
           var searchType = searchStringSplit[0]
           if(searchStringSplit.length == 2){ //is this a structure search
            searchType = searchType.toLowerCase()
            switch(searchType){ //must be one of these to qualify as a structure search
             case 'exact':
             case 'substructure':
             case 'superstructure':
             case 'similarity':
              return 'STRUCTURE'
             break;
            }
           }
           return "REGULAR" //this a regular search
        }
    </r:script>

    <title>BARD Home</title>
</head>
