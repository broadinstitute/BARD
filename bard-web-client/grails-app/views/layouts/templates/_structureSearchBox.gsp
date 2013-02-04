<%@ page import="bard.core.rest.spring.util.StructureSearchParams" %>

%{--<script type="text/javascript" src="${resource(dir: 'js/dojo-min/dojo', file: 'dojo.js')}"></script>--}%
%{--<script type="text/javascript" src="${resource(dir: 'js/jsDraw', file: 'Scilligence.JSDraw2.js')}"></script>--}%
%{--<script type="text/javascript" src="${resource(dir: 'js/jsDraw', file: 'license.js')}"></script>--}%
<script type="text/javascript" src="${request.contextPath}/js/dojo-min/dojo/dojo.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/jsDraw/Scilligence.JSDraw2.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/jsDraw/license.js"></script>
<r:require modules="structureSearch"/>

<g:hiddenField name="searchTypes" id="searchTypes" value="${StructureSearchParams.Type.values().join(':')}"/>