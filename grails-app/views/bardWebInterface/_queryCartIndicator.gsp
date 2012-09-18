<div id="summaryView">
    <div class="row-fluid">
        <div class="span12 center">
            <h3>Query Cart</h3>
            <a class="trigger btn btn-mini" href="#">View/edit</a>
            <div class="dropdown">
                <a class="btn btn-mini dropdown-toggle" data-toggle="dropdown" role="button" data-target="#">
                    Visualize <span class="caret"></span>
                </a>
                <ul class="dropdown-menu pull-right" role="menu">
                    <li><a href="/bardwebquery/MolSpreadSheet">Molecular Spreadsheet</a></li>
                    <li><a href="#">Advanced Analysis Client</a></li>
                    <li><a href="/bardwebquery/MolSpreadSheet/demo">[Molecular Spreadsheet demo]</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="row-fluid">
        <div class="span12">
            <ul class="unstyled horizontal-list">
                <g:if test="${!totalItemCount}">
                    <li>Empty</li>
                </g:if>
                <g:if test="${numberOfAssays>0}">
                    <li>${numberOfAssays} ${numberOfAssays==1?'assay definition':'assay definitions'}</li>
                </g:if>
                <g:if test="${numberOfCompounds>0}">
                    <li>${numberOfCompounds} ${numberOfCompounds==1?'compound':'compounds'}</li>
                </g:if>
                <g:if test="${numberOfProjects>0}">
                    <li>${numberOfProjects} ${numberOfProjects==1?'project':'projects'}</li>
                </g:if>
            </ul>
        </div>
    </div>
</div>
