<%@page defaultCodec="none"%>
<%@ page import="bardqueryapi.IDSearchType" %>

<r:require module="idSearch"/>
<div class="modal hide" id="idModalDiv">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">×</a>

        <h3>Enter a Comma separated list of IDs</h3>
    </div>

    <div class="modal-body">
        <textarea class="field span12" id="idSearchString" name="idSearchString" rows="15"></textarea>
    </div>

    <div class="modal-footer">
        <g:form name="idSearchForm" class="form-inline">
            <div>
                <g:radioGroup name="idSearchType"
                              values="${IDSearchType.values()}"
                              value="${IDSearchType.ALL}"
                              labels="${IDSearchType.values().label}">
                    <label class="radio inline">
                        ${it.radio} ${it.label}
                    </label>
                </g:radioGroup>
            </div>

            <br>

            <div>
                <a href="#" class="btn" data-dismiss="modal" id="closeButton2">Close</a>
                <a href="#" id="idSearchButton" class="btn btn-primary" data-dismiss="modal">Search</a>
            </div>
        </g:form>
    </div>

</div>
