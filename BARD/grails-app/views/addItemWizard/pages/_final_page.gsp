<%
    /**
     * last wizard page / tab
     *
     * @author Jeroen Wesbeek <work@osx.eu>
     * @package AjaxFlow
     */
%>
<af:page>

    <div class="alert alert-success">
        <button type="button" class="close" data-dismiss="alert">×</button>
        <strong>The item has been successfully saved.</strong>
    </div>

    <g:set var="valueText" value="${valueQualifier + " " + valueName + " " + valueUnits}"/>
    <g:render template="common/itemWizardSelectionsTable"
              model="['attribute': attributeName, 'valueType': valueTypeOption, 'value': valueText]"/>

</af:page>
