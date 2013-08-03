<div class="container-fluid">
    <div class="row-fluid">
        <div class="span6">
            <h2>ADID ${assayOneADID}: ${assayOneName}</h2>
            <h4>Cards unique to ADID ${assayOneADID}</h4>
        </div>
        <div class="span6">
            <h2>ADID ${assayTwoADID}: ${assayTwoName}</h2>
            <h4>Cards unique to ADID ${assayTwoADID}</h4>
        </div>
    </div>
</div>
<div class="container-fluid">
    <div class="row-fluid">
        <div id="cardHolder" class="span6">
            <g:each in="${exclusiveToAssayOne}" var="entry">
                <g:render template="generateAssayCompareReportCard"
                          model="[contextName: entry.key,
                                  contextItemDTOs: entry.value]"/>
            </g:each>
        </div>
        <div id="cardHolder2" class="span6">
            <g:each in="${exclusiveToAssayTwo}" var="entry">
                <g:render template="generateAssayCompareReportCard"
                          model="[contextName: entry.key,
                                  contextItemDTOs: entry.value]"/>
            </g:each>
        </div>
    </div>
</div>

