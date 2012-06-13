<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to Grails</title>
    <style type="text/css" media="screen">
    #status {
        background-color: #eee;
        border: .2em solid #fff;
        margin: 2em 2em 1em;
        padding: 1em;
        width: 12em;
        float: left;
        -moz-box-shadow: 0px 0px 1.25em #ccc;
        -webkit-box-shadow: 0px 0px 1.25em #ccc;
        box-shadow: 0px 0px 1.25em #ccc;
        -moz-border-radius: 0.6em;
        -webkit-border-radius: 0.6em;
        border-radius: 0.6em;
    }

    .ie6 #status {
        display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
    }

    #status ul {
        font-size: 0.9em;
        list-style-type: none;
        margin-bottom: 0.6em;
        padding: 0;
    }

    #status li {
        line-height: 1.3;
    }

    #status h1 {
        text-transform: uppercase;
        font-size: 1.1em;
        margin: 0 0 0.3em;
    }

    #page-body {
        margin: 2em 1em 1.25em 2em;
    }

    h2 {
        margin-top: 1em;
        margin-bottom: 0.3em;
        font-size: 1em;
    }

    p {
        line-height: 1.5;
        margin: 0.25em 0;
    }

    #controller-list ul {
        list-style-position: inside;
    }

    #controller-list li {
        line-height: 1.3;
        list-style-position: inside;
        margin: 0.25em 0;
    }

    @media screen and (max-width: 480px) {
        #status {
            display: none;
        }

        #page-body {
            margin: 0 1em 1em;
        }

        #page-body h1 {
            margin-top: 0;
        }
    }
    </style>
</head>

<body>

<div id="page-body" role="main">
    <h1>Testing the data export API</h1>

    <p><b>You can use CURL or any browser plugin that allows you to call REST APIs. <br/>
        Google has a couple (Postman, RestConsole, Advanced Rest Client)<br/>
        Firefox has the RestClient Plugin ( I am sure there are others).</b>

    <p>
        <b>The table below lists the available resources and the Accept Types that it requires. The Accept string goes in the header of most client apps.<br/>
            <br/>
        </b>
    </p>
    <br/>
    <br/>

    <div id="controller-list" role="navigation">
        <h3>Root Service</h3>
        <table>
            <thead><th>Resource</th><th>URL</th><th>Accept Header</th><th>Comments</th></thead>
            <tbody>
            <tr><td>Root Service</td><td><g:link
                    mapping="api">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=bardexport</td><td>This is the only URL that we expose to clients</td>
            </tr>
               </tbody>
        </table>
        <br/>
        <br/>


        <h3>Dictionary</h3>
        <table>
            <thead><th>Resource</th><th>URL</th><th>Accept Header</th><th>Comments</th></thead>
            <tbody>
            <tr><td>Dictionary</td><td><g:link
                    mapping="dictionary">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=dictionary</td><td>Returns every thing in the dictionary</td>
            </tr>
            <tr><td>Result Type</td><td><g:link mapping="resultType"
                                                params="[id: 1]">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=resultType</td><td>Replace the resultTypeId value with any valid Id</td>
            </tr>
            <tr><td>Stage</td><td><g:link mapping="stage"
                                          params="[id: 1]">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=stage</td><td>Replace the stageId value with any valid Id</td>
            </tr>
            <tr><td>Element</td><td><g:link mapping="element"
                                            params="[id: 1]">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=element</td><td>Replace the elementId value with any valid Id</td>
            </tr>

            </tbody>
        </table>
        <br/>
        <br/>

        %{--<h3>Projects and Assays</h3>--}%
        %{--<table>--}%
            %{--<thead><th>Resource</th><th>URL</th><th>Accept Header</th><th>Comments</th></thead>--}%
            %{--<tbody>--}%
            %{--<tr><td>All Projects</td><td><g:link--}%
                    %{--mapping="cap">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=cap</td><td>Returns All Projects</td>--}%
            %{--</tr>--}%
            %{--<tr><td>Projects Not Uploaded</td><td></td><td>application/vnd.bard.cap+xml;type=projects</td><td>This is not yet implemented because of changes in data model</td>--}%
            %{--</tr>--}%
            %{--<tr><td>Assay</td><td><g:link mapping="assay"--}%
                                          %{--params="[id: 1]">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=assay</td><td>Replace the assayId value with any valid Id</td>--}%
            %{--</tr>--}%
            %{--<tr><td>Project</td><td><g:link mapping="project"--}%
                                            %{--params="[id: 1]">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=project</td><td>Replace projectId value with any valid Id</td>--}%
            %{--</tr>--}%

            %{--</tbody>--}%
        %{--</table>--}%
        <br/>
        <br/>

        %{--<h3>Experiments and Results</h3>--}%
        %{--<table>--}%
            %{--<thead><th>Resource</th><th>URL</th><th>Accept Header</th><th>Comments</th></thead>--}%
            %{--<tbody>--}%
            %{--<tr><td>All Experiments</td><td><g:link--}%
                    %{--mapping="experiments">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=experiments</td><td>Returns All Experiments (Should really return experiments that have not been uploaded)</td>--}%
            %{--</tr>--}%
            %{--<tr><td>Experiment</td><td><g:link mapping="experiment"--}%
                                               %{--params="[id: 1]">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=experiment</td><td>A particular experiment</td>--}%
            %{--</tr>--}%
            %{--<tr><td>Results</td><td><g:link mapping="results"--}%
                                            %{--params="[id: 1]">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=results</td><td>Results of a particular experiment</td>--}%
            %{--</tr>--}%
            %{--<tr><td>Result</td><td><g:link mapping="result"--}%
                                           %{--params="[id: 1]">Copy this link</g:link></td><td>application/vnd.bard.cap+xml;type=result</td><td>A result</td>--}%
            %{--</tr>--}%

            %{--</tbody>--}%
        %{--</table>--}%
    </div>
</div>
</body>
</html>
