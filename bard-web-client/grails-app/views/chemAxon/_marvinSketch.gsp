<%-- Sets up the MarvinSketch applet. Additional MarvinSketch param could be set here --%>
<script type="text/javascript" SRC="${request.contextPath}/marvin/marvin.js"></script>
<script type="text/javascript" src="${request.contextPath}/marvin/js2java.js"></script>
<script type="text/javascript">
    %{-- if java is enabled document.write("Is Java enabled? " + navigator.javaEnabled() );--}%
    var isJs2Java = isLiveConnect();
    var SELECTED = false
    msketch_mayscript = true;
    msketch_name = "MarvinSketch";
    msketch_begin("${request.contextPath}/marvin", window.innerWidth / 2, window.innerHeight / 2);

    //We set the smiles to a dummy C
    msketch_param("mol", 'C');
    msketch_param('molLoaderFinishedEvent', 'javascript:insertCurrentSmiles();');
    msketch_end();

    //If there is a smiles tring in the search box insert it
    function insertCurrentSmiles() {
        if (!SELECTED) {
            var currentSearch = $('#searchString').val();
            //extract smiles from search string
            var smiles = extractSmilesFromSearchString(currentSearch)
            var structureSearchType = extractStructureSearchTypeFromSearchString(currentSearch)
            if(smiles.length){
                importMolToMSketch(null,smiles,document.MarvinSketch,"MarvinSketch")
              } else{
                importMolToMSketch(null,smiles,document.MarvinSketch,"MarvinSketch")
            }
            switch (structureSearchType) { //must be one of these to qualify as a structure search

                case 'Exact': //this is an exact search
                    $("input[name='structureSearchType'][value='Exact']").attr("checked", "checked");
                    break;
                case 'Substructure':
                    $("input[name='structureSearchType'][value='Substructure']").attr("checked", "checked");
                    break;
                case 'Similarity':  //this is an project search with ids
                    $("input[name='structureSearchType'][value='Similarity']").attr("checked", "checked");
                    break;
                case 'Superstructure':
                    $("input[name='structureSearchType'][value='Superstructure']").attr("checked", "checked");
                    break;

            }
            SELECTED = true //otherwise the applet keeps reloading itself
        }

    }
    function importMolToMSketch(opts, smiles, targetMSketchDomObject, targetMSketchName) {

        if(!smiles.length){
            targetMSketchDomObject.setMol(null, null);
            return;
        }
        if((targetMSketchDomObject != null) && isJs2Java) {
            targetMSketchDomObject.setMol(smiles, opts);
        } else if(!isJs2Java) {
            mparams = "java.lang.String";
            if(opts != null) {
                mparams += ",java.lang.String";
            }
            setMethod(targetMSketchName + ".setMol",mparams);
            addMethodParam(smiles);
            if(opts != null) {
                addMethodParam(opts);
            }
            runMethod();
        } else {
            alert("Cannot import molecule:\n"+
                    "no JavaScript to Java communication in your browser.\n");
        }
    }
    function extractSmilesFromSearchString(searchString) {
        if (searchString.length) {
            var searchStringSplit = searchString.split(":");
            if (searchStringSplit.length == 2 && $.trim(searchStringSplit[1]).length) { //has to be of the form Exact:CCC so there must be 2 things in the array
                return searchStringSplit[1];
            }
        }
        return ""

    }
    /**
     * Find the structure search Type
     * @param searchString
     * @return {String}
     */
    function extractStructureSearchTypeFromSearchString(searchString) {
        //we want to find out if this is a Structure search
        var structureSearchType = "Substructure"
        if (searchString.length) {
            var searchStringSplit = searchString.split(":");
            var searchType = searchStringSplit[0];
            if (searchStringSplit.length == 2 && $.trim(searchStringSplit[1]).length) { //has to be of the form Exact:CCC so there must be 2 things in the array
                searchType = searchType.toLowerCase();
                var stringAfterColon = $.trim(searchStringSplit[1])
                switch (searchType) { //must be one of these to qualify as a structure search
                    case 'exact':
                        structureSearchType = "Exact";
                        break;
                    case 'substructure':
                        structureSearchType = "Substructure";
                        break;
                    case 'superstructure':
                        structureSearchType = "Superstructure";
                        break;
                    case 'similarity':
                        structureSearchType = "Similarity";
                        break;

                }
            }
        }
        return structureSearchType
    }
</script>


