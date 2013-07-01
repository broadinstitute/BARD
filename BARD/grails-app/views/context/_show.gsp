<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%-- A template for showing summary for both project and assay def --%>
<div id="cardView" class="cardView" class="row-fluid">
    <g:if test="${!uneditable}">
        <g:if test="${editable == 'canedit'}">
            <div class="span12">
                <g:link action="editContext" id="${contextOwner?.id}"
                        class="btn">Edit</g:link>
            </div>
        </g:if>
    </g:if>
    <div class="row-fluid">

        <g:render template="/context/biology"
                  model="[contextOwner: contextOwner, biology: contextOwner.groupBiology(), subTemplate: 'show', renderEmptyGroups: false]"/>
        <div id="cardHolder" class="span12">
            <g:render template="/context/currentCard"
                      model="[contextOwner: contextOwner, currentCard: contextOwner.groupAssayProtocol(), subTemplate: 'show', renderEmptyGroups: false]"/>
        </div>


        <g:render template="/context/currentCard"
                  model="[contextOwner: contextOwner, currentCard: contextOwner.groupAssayDesign(), subTemplate: 'show', renderEmptyGroups: false]"/>


        <g:render template="/context/currentCard"
                  model="[contextOwner: contextOwner, currentCard: contextOwner.groupAssayReadout(), subTemplate: 'show', renderEmptyGroups: false]"/>

        <g:render template="/context/currentCard"
                  model="[contextOwner: contextOwner, currentCard: contextOwner.groupAssayComponents(), subTemplate: 'show', renderEmptyGroups: false]"/>


        <g:if test="${contextOwner.groupUnclassified()}">
            <div id="cardHolder" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: contextOwner, currentCard: contextOwner.groupUnclassified(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </g:if>
    </div>
</div>