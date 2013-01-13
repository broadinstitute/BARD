<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%-- A template for showing summary for both project and assay def --%>
<div id="cardView" class="cardView" class="row-fluid">
    <div class="row-fluid">
        <g:render template="/context/list"
                  model="[contextOwner: contextOwner, contexts: contexts, subTemplate: 'show']"/>
    </div>
</div>