%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<g:if test="${'edit'.equals(subTemplate)}">
    <div class="row-fluid">
        <div class="span12">
            <g:link controller="context" action="addCard"
                    params="[contextClass: contextOwner.class.simpleName + 'Context', ownerId: contextOwner.id, cardSection: currentCard.key]"
                    class="btn"><i class="icon-plus"></i>Add Card</g:link>
            <br/><br/>
        </div>
    </div>
</g:if>
<g:if test="${currentCard || (subTemplate.equals("edit") && contextOwner instanceof bard.db.registration.Assay)}">
    <g:if test="${currentCard?.value}">
        <div id="cardHolder" class="span12">
            <div class="row-fluid">
                <g:each in="${contextOwner.splitForColumnLayout(currentCard.value)}" var="contextColumnList">
                    <div class="span6">
                        <g:each in="${contextColumnList}" var="context">
                            <g:if test="${showCheckBoxes}">
                            %{--for the case where we want to display only the contexts with at least one context item that has a non fixed attribute--}%
                                <g:if test="${context.atLeastOneNonFixedContextItem()}">
                                    <g:render template="../contextItem/${subTemplate}"
                                              model="[
                                                      contextOwner: contextOwner,
                                                      context: context,
                                                      cardSection: currentCard.key,
                                                      showCheckBoxes: showCheckBoxes,
                                                      existingContextIds: existingContextIds
                                              ]"/>
                                </g:if>

                            </g:if>
                            <g:else>
                                <g:render template="../contextItem/${subTemplate}"
                                          model="[contextOwner: contextOwner,
                                                  context: context,
                                                  cardSection: currentCard.key,
                                                  showCheckBoxes: showCheckBoxes,
                                                  existingContextIds: existingContextIds]"/>
                            </g:else>
                        </g:each>
                    </div>
                </g:each>
            </div>
        </div>
    </g:if>
</g:if>
