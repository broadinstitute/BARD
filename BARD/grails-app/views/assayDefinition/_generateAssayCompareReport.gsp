<div class="container-fluid">
    <div class="row-fluid">
        <div class="span4">
        </div>
        <div class="span4" style="margin: auto 0;">
            <table class="colorkey">
                <tr>
                    <td>Legend:  </td>
                    <td style="background-color: #FFD5D5"> Colored Cells, shows items that are not common to both assays</td>
                </tr>
            </table>
        </div>
        <div class="span4">
        </div>
    </div>
</div>


<div class="container-fluid">
    <div class="row-fluid">
        <div class="span6">
            <h2>ADID ${assayOneADID}: ${assayOneName}</h2>
            <h4>Cards unique to ADID:
                <g:link action="show" controller="assayDefinition" id="${assayOneADID}">${assayOneADID}</g:link></h4>
        </div>

        <div class="span6">
            <h2>ADID ${assayTwoADID}: ${assayTwoName}</h2>
            <h4>Cards unique to ADID:
                <g:link action="show" controller="assayDefinition" id="${assayTwoADID}">${assayTwoADID}</g:link></h4>
        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div id="cardHolder" class="span6">
            <g:each in="${exclusiveToAssayOne}" var="entry">
                <g:render template="generateAssayCompareReportCard"
                          model="[contextName: entry.key.contextName,
                                  contextItemDTOs: entry.value]"/>
            </g:each>
        </div>

        <div id="cardHolder2" class="span6">
            <g:each in="${exclusiveToAssayTwo}" var="entry">
                <g:render template="generateAssayCompareReportCard"
                          model="[contextName: entry.key.contextName,
                                  contextItemDTOs: entry.value]"/>
            </g:each>
        </div>
    </div>
</div>

