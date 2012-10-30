<div id="cardView" class="cardView" class="row-fluid">

    <div class="span12">

        <div class="row-fluid">
            <div id="cardHolder" class="span12">
                <g:each in="${cardDtoMap}" var="entry">
				    <div id="${entry.key}"  class="roundedBorder card-group ${entry.key.replaceAll(/( |> )/, '-')}">
				
				        <div class="row-fluid">
				            <h5 class="span12">${entry.key}</h5>
				        </div>
				
				        <div class="row-fluid">
				            <g:each in="${entry.value}" status="cardIndex" var="card">
				                <g:if test="${(cardIndex % 2) == 0 && cardIndex != 0}">
				                    </div><div class="row-fluid">
				                </g:if>
				                <div id="card-${card.id}" class="span6 card roundedBorder card-table-container">
								    <table class="table table-hover">
								        <caption id="${card.id}" class="assay_context">
								            <div class="cardTitle">${card.label}</div>								            
								        </caption>
								        <tbody>
								        <g:each in="${card.lines}" status="i" var="line">
								            <tr id="${line.id}" class='context_item_row'>
								                <td class="attributeLabel">${line.attributeLabel}</td>
								                <td class="valuedLabel">${line.valueLabel}</td>								                
								            </tr>
								        </g:each>
								        </tbody>
								    </table>
								</div>
				            </g:each>
				        </div>
				
				    </div>
				</g:each>
            </div>
        </div>

    </div>
</div>
