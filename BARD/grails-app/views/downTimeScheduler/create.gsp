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
<%
    Date startDate = new Date()
    Date endDate = startDate + 1

%>
<r:script type="text/javascript">
    $('.form_datetime').datetimepicker({
        format: 'mm/dd/yyyy hh:mm P',
        startDate: '+0d',
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