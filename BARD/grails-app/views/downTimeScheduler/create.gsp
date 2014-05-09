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

<%@ page import="java.text.SimpleDateFormat; bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,dateTimePicker"/>
    <meta name="layout" content="main"/>
    <title>Schedule Down Time</title>

</head>


<body>
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="save" controller="downTimeScheduler">
            <h3>Schedule Down Time</h3>

            <div class="control-group ${hasErrors(bean: downTimeSchedulerCommand, field: 'downTimeAsDate', 'error')}">
                <p>This is the time the downtime message will expire.   If you select 10pm, then the message will stop appearing at 10pm.</p>
                <label class="control-label" for="downTimeAsString">
                    <g:message code="downtimescheduler.date.time" default="Date Time"/>:</label>

                <div class="controls">
                    <div class="input-append date form_datetime">
                        <input name="downTimeAsString" class="input-xxlarge" type="text" value="${downTimeSchedulerCommand?.downTimeAsString}" readonly>
                        <span class="add-on"><i class="icon-remove"></i></span>
                        <span class="add-on"><i class="icon-calendar"></i></span>
                        <span class="help-inline"><g:fieldError field="downTimeAsString"
                                                                bean="downTimeSchedulerCommand"/></span>
                    </div>
                </div>
            </div>

            <p>The following should be the message that you want to appear on each page.  If writing a message about
            scheduled maintenance, you should include the time of the downtime in the message.  (The value selected above is
            not shown to the user)</p>
            <p>There is a 100 character limit for the downtime message.</p>
            <div class="control-group ${hasErrors(bean: downTimeSchedulerCommand, field: 'displayValue', 'error')}">
                <label class="control-label" for="displayValue">
                    <g:message code="downtimescheduler.displayvalue.label" default="Message"/>:</label>

                <div class="controls">
                    <g:textArea rows="5" class="input-xxlarge" id="displayValue" name="displayValue"
                                value="${downTimeSchedulerCommand?.displayValue}" required=""/>
                    <span class="help-inline"><g:fieldError field="displayValue"
                                                            bean="downTimeSchedulerCommand"/></span>
                </div>
            </div>


            <g:hiddenField name="createdBy" value="${downTimeSchedulerCommand?.createdBy}"/>
            <div class="control-group">
                <div class="controls">
                    <g:link controller="downTimeScheduler" action="show"
                            class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Schedule Down Time">
                </div>
            </div>

        </g:form>
    </div>
</div>
<r:script type="text/javascript">

    $('.form_datetime').datetimepicker({
        startDate: '-1d',
        endDate: '+1m',
        autoclose: true,
        todayHighlight: true,
        todayBtn: true,
        minuteStep: 10,
        pickerPosition: "bottom-left"
    });
</r:script>
</body>
</html>
