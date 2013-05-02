<html>
<head>
    <r:require modules="core,bootstrap,newTerm"/>
    <meta name="layout" content="basic"/>
    <title>Propose new term</title>
</head>

<body>
<g:render template="/common/message"/>
<g:render template="/common/errors" model="['errors': termCommand?.errors?.allErrors]"/>
<div class="row-fluid">

    <div class="span6 offset1">
        <g:form class="form-horizontal" action="saveTerm">
            <g:hiddenField name="currentElementId" id="currentElementId"
                           value="${currentElement ? currentElement.id : ''}"/>
            <g:render template="addTermForm"/>
            <div class="control-group">
                <label>
                    <h4>5. Choose to save your proposed term.</h4>
                </label>
            </div>

            <div class="control-group">
                <div class="controls">
                    <input type="submit" class="btn btn-primary" value="save">
                    <g:link action="addTerm"
                            fragment="documents-header" class="btn">Cancel</g:link>
                </div>
            </div>
        </g:form>
    </div>

    <div class="span5">
        <r:require module="dynatree"/>
        <h3>Current BARD Hierarchy</h3>

        <div id="element-hierarchy-tree"></div>

        <div>
            <br/><strong>Note:</strong> All proposed terms will be reviewed by
        BARD Dictionary Curators and modified or recategorized as needed to fit into the BARD
        Hierarchy. They will contact you if they have questions. You will be notified once your term has been curated. In the meantime, you can continue to work
        and use your newly proposed term.
        </div>
        <r:script>
            $("#element-hierarchy-tree").dynatree({
            children: ${elementHierarchyAsJsonTree},
            onActivate: function(node) {
                $("#parentLabel").val(node.data.title);
                $("#parentDescription").val(node.data.description);
            }
           }
        );
        selectCurrentElement();

        function selectCurrentElement(){
            var currentElementId = $("#currentElementId").val();
            if(currentElementId){
               $("#element-hierarchy-tree").dynatree("getTree").activateKey(currentElementId);
            }
         }
        function trim(input) {
            var s = input.value;
            s = s.replace(/(^\s*)|(\s*$)/gi,"");
            s = s.replace(/[ ]{2,}/gi," ");
            s = s.replace(/\n /,"\n");
            input.value = s;
        }
        </r:script>
    </div>
</div>
</body>
</html>
