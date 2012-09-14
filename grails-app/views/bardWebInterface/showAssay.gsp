<%@ page import="bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="details"/>
    <title>BARD : Assay Definition : ADID ${assayAdapter?.assay.id}</title>
</head>
<body>
<div class="row-fluid" style="clear:both;">
    <div class="span3">
        <a style="float: left;" href="${createLink(controller:'BardWebInterface',action:'index')}"><img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database" /></a>
    </div>

    <div class="span7">
        <table>
            <tr>
                <td colspan="2">
                    <h1>Assay Definition Detail for ADID ${assayAdapter?.assay.id}</h1>
                </td>
            </tr>
            <tr>
                <td>
                    <h3>${assayAdapter?.assay.name}</h3>
                </td>
                <td>
                    <a href="/bardwebquery/sarCart/add/${assayAdapter?.assay.id}"
                        onclick="jQuery.ajax({  type:'POST',
                            data:{'id': '${assayAdapter?.assay.id}','class': 'class bardqueryapi.CartAssay','assayTitle':'${JavaScriptUtility.cleanup(assayAdapter?.assay.name)}','version': '0', 'stt':trackStatus},
                            url:'/bardwebquery/sarCart/add',
                            success:function(data,textStatus){
                                jQuery(ajaxLocation).html(data);
                            }
                        });
                        return false;"
                        action="add"
                        controller="sarCart"><i class="icon-shopping-cart"></i><span class="addtocartfont">&nbsp;Add to Cart</span>
                    </a>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <dl class="dl-horizontal">
                        <dt>Assay Format:</dt><dd></dd>
                        <dt>Assay Type:</dt><dd></dd>
                        <dt>Target:</dt><dd></dd>
                    </dl>
                </td>
            </tr>

        </table>





    </div>

    <div class="span2">
        <div class="well wellmod">
           <g:render template="queryCartIndicator"/>
            <div class="row-fluid">
                <h5><nobr><a class="trigger" href="#">View details/edit</a></nobr></h5>
            </div>
        </div>
    </div>

    <div class="span12 header">
    </div>
</div>
<r:script>
    $(document).ready(function () {
        $('.collapse').on('show', function () {
            var icon = $(this).siblings().find("i.icon-chevron-right");
            icon.removeClass('icon-chevron-right').addClass('icon-chevron-down');
        });
        $('.collapse').on('hide', function () {
            var icon = $(this).siblings().find("i.icon-chevron-down");
            icon.removeClass('icon-chevron-down').addClass('icon-chevron-right');
        });
    })
</r:script>
<div class="row-fluid">
    <div class="span12 accordion">
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#project-header" id="project-header" class="accordion-toggle" data-toggle="collapse" data-target="#project-info"><i class="icon-chevron-right"></i> Projects</a>
            </div>
            <div id="project-info" class="accordion-body collapse">
                <div class="accordion-inner">
                    <g:render template="assayProjects" model="['assayAdapter': assayAdapter]" />
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#assay-header" id="assay-header" class="accordion-toggle" data-toggle="collapse" data-target="#assay-bio-info"><i class="icon-chevron-right"></i> Assay and Biology Details</a>
                <div id="assay-bio-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        TBD
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="document-header" class="accordion-toggle" data-toggle="collapse" data-target="#document-info"><i class="icon-chevron-right"></i> Documents</a>
                <div id="document-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="assayDocuments" model="['assayAdapter': assayAdapter]" />
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#results-header" id="results-header" class="accordion-toggle" data-toggle="collapse" data-target="#result-info"><i class="icon-chevron-right"></i> Results</a>
                <div id="result-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        TBD
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#registration-header" id="registration-header" class="accordion-toggle" data-toggle="collapse" data-target="#registration-info"><i class="icon-chevron-right"></i> Registration Info</a>
                <div id="registration-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="assaySummary" model="['assayAdapter': assayAdapter]" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>