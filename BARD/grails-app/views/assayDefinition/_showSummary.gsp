<%--
  Created by IntelliJ IDEA.
  User: ddurkin
  Date: 1/17/13
  Time: 3:29 PM
  To change this template use File | Settings | File Templates.
--%>

<div id="showSummary">
    <r:require modules="summary"/>
    <div class="span12"><button id="editSummaryButton" class="btn btn-primary">Edit</button>
    </div>
    <g:render template='editSummary' model="['assay': assayInstance]"/>
    <g:render template="summaryDetail" model="['assay': assayInstance]"/>
</div>