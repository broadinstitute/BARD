<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%-- A template for showing summary for both project and assay def --%>
<div id="cardView" class="cardView" class="row-fluid">
    <div class="span12">
        <g:link action="editContext" id="${contextOwner?.id}"
                class="btn btn-primary">Edit</g:link>
    </div>
    <div class="row-fluid">
        <g:render template="/context/list"
                  model="[contextOwner: contextOwner, contexts: contexts, subTemplate: 'show', renderEmptyGroups: false]"/>
    </div>
</div>