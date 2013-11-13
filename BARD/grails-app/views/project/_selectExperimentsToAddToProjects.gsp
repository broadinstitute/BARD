<%@ page import="bard.db.dictionary.StageTree" %>
<g:if test="${command.availableExperiments}">
   <g:hiddenField name="validateExperimentIds" value="true"/>
    <br/>
    <h5><div id="selectExperimentsId">Select Experiments</div></h5>
    <g:select name="experimentIds" id="experimentId" required=""  class="input-xxlarge"
              from="${command.availableExperiments}"
              multiple="true"
              optionKey="id" optionValue="displayName"/>

</g:if>
