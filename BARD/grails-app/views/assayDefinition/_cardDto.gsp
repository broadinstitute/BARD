<div id="card-${card.id}" class="span6 card roundedBorder card-table-container">
    <table class="table table-hover">
        <caption id="${card.id}" class="assay_context">
            <div class="cardTitle">${card.label}</div>
            <div class="cardMenu">
            	<div class="btn-group">
                	%{-- <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="caret"></span></a> --}%
                	<a class="btn btn-info dropdown-toggle" data-toggle="dropdown" href="#"><span class="icon-cog"></span></a>
                	<ul class="dropdown-menu" style="z-index:3999;">
					    <li><a href="#" onclick="editCardName(${card.id}, '${card.label}')"><i class="icon-pencil"></i> Edit card name</a></li>
					    <li><a href="#" onclick="launchAddItemWizard(${card.id})"><i class="icon-road"></i> Add item wizard</a></li>
					    <g:if test="${card.lines.size() == 0}">
					    	<li><a href="#" onclick="deleteCard(${card.id})"><i class="icon-pencil"></i> Delete card</a></li>
					    </g:if>					    
					</ul>
                </div>
            </div>
            
            %{-- <div><button id="b-${card.id}" class="btn btn-mini">Edit</button></div> --}%
            %{-- 
            <g:if test="${card.lines.size() == 0}">
                <div class="deleteCardButton"><button id="${card.id}"><i class="icon-remove"></i></button></div>                
            </g:if>
             --}%
        </caption>
        <tbody>
        <g:each in="${card.lines}" status="i" var="line">
            <tr id="${line.id}" class='context_item_row'>
                <td class="attributeLabel">${line.attributeLabel}</td>
                <td class="valuedLabel">${line.valueLabel}</td>
                %{-- <td class="deleteItemButton"><button id="${line.id}"><i class="icon-remove"></i></button></td> --}%
                <td class="deleteItemButton">
                	<div class="btn-group">
                	<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="caret"></span></a>
                	<ul class="dropdown-menu">
					    <li><a href="#"><i class="icon-pencil"></i> Edit</a></li>					    
					    <li><a href="#" onclick="moveCardItem(${assayId}, ${line.id})"><i class="icon-move"></i> Move</a></li>
					    <li><a href="#" onclick="deleteCardItem(${line.id}, ${card.id})"><i class="icon-trash"></i> Delete</a></li>
					</ul>
                	</div>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
