<%--
  Created by IntelliJ IDEA.
  User: ddurkin
  Date: 1/17/13
  Time: 3:29 PM
  To change this template use File | Settings | File Templates.
--%>
<r:require modules="summary"/>
<div>
    <div class="span12"><button id="editSummaryButton" class="btn btn-primary">Edit</button>
    </div>
    <g:render template='editSummary'/>
    <g:render template="summaryDetail" model="['assay': assayInstance]"/>
</div>