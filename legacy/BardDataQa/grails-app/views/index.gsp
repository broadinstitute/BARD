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
    <title>Monitoring the BARD Data QA Load Process</title>
    <h1>Monitoring the BARD Data QA Load Process</h1>
</head>
<body>

    <h2>Available pages / actions:</h2>
    <table cellpadding="10" cellspacing="1">
        <tr>
            <td>Issue tracking</td>
            <td><a href="https://jira.broadinstitute.org/secure/IssueNavigator.jspa?sorter/field=updated&sorter/order=DESC">JIRA list of issues</a></td>
        </tr>
        <tr>
            <td>Datasets</td>
            <td><g:link controller="dataset" action="index">List datasets and calculate marginal product of work for them</g:link></td>
        </tr>
        <tr>
            <td>Missing AID's</td>
            <td><g:link controller="checkForMissingAids" action="index">Check for AID's that are in a dataset but missing from external_reference table (${missingAidCount})</g:link></td>
        </tr>
        <tr>
            <td>Projects, Assays and their Centers</td>
            <td><g:link controller="projectAssaysCenters" action="index">List assays associated with each project and the center for each</g:link></td>
        </tr>
        <tr>
            <td>AID's in MAAS</td>
            <td><g:link controller="aidSpreadsheet" action="index">List AID's and the spreadsheets and datasets they are in</g:link></td>
        </tr>
        <tr>
            <td>QA Status of Projects</td>
            <td><g:link controller="projectStatus" action="index">List, modify, change QA status of projects</g:link></td>
        </tr>
        <tr>
            <td>AID's in CAP Production</td>
            <td><g:link controller="aidInProd" action="index">List AID's that currently exist in CAP Production</g:link> </td>
        </tr>
    </table>
</body>
</html>
