<%@ page import="bard.db.enums.AddChildMethod" %>
<div class="span5">
    <h3>Current BARD Hierarchy</h3>

    <div>
        <ul>
            <li><g:img uri="/js/dynatree-1.2.2/skin/cannot_add_children.png"/> ${AddChildMethod.NO.description}</li>
            <li><g:img uri="/js/dynatree-1.2.2/skin/rdm_request.jpg"/> ${AddChildMethod.RDM_REQUEST.description}</li>
            <li><g:img uri="/js/dynatree-1.2.2/skin/add_children.jpg"/> ${AddChildMethod.DIRECT.description}</li>
        </ul>
    </div>

    <div class="control-group">
        <div class="controls">
            <label class="checkbox">
                <g:checkBox id="doNotShowRetiredTerms" name="doNotShowRetiredTerms" checked="False" onclick="reloadTree();" onchange="reloadTree();"/>
                <strong>Do NOT show 'Retired' terms in hierarchy</strong>
            </label>
        </div>
    </div>
    <%--
    <form>
        <input type="checkbox" id="doNotShowRetiredTerms" name="doNotShowRetiredTerms" checked="False" onclick="createHierarchyTree();" onchange="createHierarchyTree();">
        <strong>Do NOT show 'Retired' terms in hierarchy</strong>
    </form>
    --%>
    <div id="element-hierarchy-tree"></div>

    <div>
        <br/><strong>Note:</strong> All proposed terms will be reviewed by
    BARD Dictionary Curators and modified or recategorized as needed to fit into the BARD
    Hierarchy. They will contact you if they have questions. You will be notified once your term has been curated. In the meantime, you can continue to work
    and use your newly proposed term.
    </div>
</div>

