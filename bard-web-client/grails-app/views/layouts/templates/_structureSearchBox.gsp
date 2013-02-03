<%@ page import="bard.core.rest.spring.util.StructureSearchParams" %>

<script type="text/javascript" src="js/dojo-min/dojo/dojo.js"></script>
<script type="text/javascript" src="js/jsDraw/Scilligence.JSDraw2.js"></script>
<script type="text/javascript" src="js/jsDraw/license.js"></script>

<r:require modules="structureSearch"/>

<g:hiddenField name="searchTypes" id="searchTypes" value="${StructureSearchParams.Type.values().join(':')}"/>