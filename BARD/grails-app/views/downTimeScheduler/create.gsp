<%@ page import="java.text.SimpleDateFormat; bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap"/>
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
                    <div id="datetimepicker" class="input-append date-selection">
                        <input name="downTimeAsString" data-format="MM/dd/yyyy HH:mm PP"
                               type="text" placeholder="Click icon to select date"/>
                        <span class="add-on">
                            <i data-time-icon="icon-time" data-date-icon="icon-calendar">
                            </i>
                        </span>
                        <span class="help-inline"><g:fieldError field="downTimeAsString"
                                                                bean="downTimeSchedulerCommand"/></span>
                    </div>
                </div>
            </div>
            <p>Note: That whatever you enter in the text box would be prepended to the selected date and used as display for the user! </p>
            <div class="control-group ${hasErrors(bean: downTimeSchedulerCommand, field: 'displayValue', 'error')}">
                <label class="control-label" for="displayValue">
                    <g:message code="downtimescheduler.displayvalue.label" default="Message"/>:</label>

                <div class="controls">
                    <g:textField rows="10" class="input-xxlarge" id="displayValue" name="displayValue"
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
<script type="text/javascript"
        src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.3/jquery.min.js">
</script>
<script type="text/javascript"
        src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js">
</script>
<script type="text/javascript"
        src="http://tarruda.github.com/bootstrap-datetimepicker/assets/js/bootstrap-datetimepicker.min.js">
</script>
<script type="text/javascript"
        src="http://tarruda.github.com/bootstrap-datetimepicker/assets/js/bootstrap-datetimepicker.pt-BR.js">
</script>
<script type="text/javascript">
    $('#datetimepicker').datetimepicker({
        format: 'MM/dd/yyyy hh:mm PP',
        language: 'en'
    });
</script>
</body>
</html>