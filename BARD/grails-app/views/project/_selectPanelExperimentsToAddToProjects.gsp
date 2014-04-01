<%@ page import="bard.db.dictionary.StageTree" %>
<g:if test="${command.availablePanelExperiments}">
   <g:hiddenField name="validatePanelExperimentIds" value="true"/>
    <br/>
    <h5><div id="selectPanelExperimentsId">Select Experiments</div></h5>
    <g:select name="panelExperimentIds" id="panelExperimentIds" required=""  class="input-xxlarge"
              from="${command.availablePanelExperiments}"
              multiple="true"
              optionKey="id" optionValue="displayName"/>

</g:if>
