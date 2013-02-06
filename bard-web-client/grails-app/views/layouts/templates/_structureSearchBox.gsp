<%@ page import="bard.core.rest.spring.util.StructureSearchParams" %>

<r:require modules="structureSearch"/>

<g:hiddenField name="searchTypes" id="searchTypes" value="${StructureSearchParams.Type.values().join(':')}"/>