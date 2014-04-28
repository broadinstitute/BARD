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

<!DOCTYPE html>
<html>
<head>
    <%-- Sitemesh does not appear to get applied to this page.  It's unclear to me why, but quick googling suggests other people have this problem.
    It's a little unclear to me whether its simply an unresolved issue or whether it has been fixed.   This issue appears to still be
    open: http://jira.grails.org/browse/GRAILS-1844 --%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>BARD : An error has occurred</title>

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">
            <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
                <img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/>
            </a>
        </div>
        <div class="span6">
        </div>
    </div>

    <div class="row-fluid">
        <div class="span12">
            <h1>An error has occurred</h1>

            <p>
                (Error ID: ${errorId})
            </p>

            <p>
            BARD has encountered an internal error when trying to process your request.  We apologize for the inconvenience.  An email has been sent to the BARD Development Team, and we will investigate the problem as soon as possible.
            </p>

            <p>
                In the meantime, you can try:
                <ul>
                    <li>
                        Waiting a little bit and then repeating what you were doing, in case the error is only temporary.
                    </li>
                    <li>
                        Reporting the problem to the bard-users mailing list with a description of what you are trying to do.  This will help us to resolve the problem more quickly.
                    </li>
                </ul>
            </p>

            <p>
                Thank you for your patience.
            </p>

            <p>
                Sincerely,
                <br>
                The BARD Development Team
            </p>

            <g:if test="${showException}">
                <g:renderException exception="${exception}" />
            </g:if>

            <%--
            <p>
                If this error is reoccurring, please let us know what you were doing at the time of the error.
            </p>

            <g:form controller="errors" action="submitFeedback" method="POST">
                <input type="hidden" value="${errorId}" name="errorId">
                <input type="text" name="" value="">
                <input type="text" name="" value="">
                <textarea>
                </textarea>
                <g:submitButton name="Submit Feedback"></g:submitButton>
            </g:form>

            --%>
        </div>
    </div>
</div>
</body>
</html>
