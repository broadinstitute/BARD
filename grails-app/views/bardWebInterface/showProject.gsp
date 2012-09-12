<%@ page import="bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="details"/>
    <title>BARD : Project : ID ${projectAdapter?.project?.id}</title>
    <r:script>
	$(document).ready(function() {
		$( "#accordion" ).accordion({ autoHeight: false });
	}) 
</r:script>

</head>
<body>

<div class="row-fluid" style="clear:both;">
    <div class="span3">
        <a style="float: left;" href="${createLink(controller: 'BardWebInterface', action: 'index')}"><img
                src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/></a>
    </div>

    <div class="span7">
        <table>
            <tr>
                <td colspan="2">
                    <h1>Project Detail for ID ${projectAdapter?.project?.id}</h1>
                </td>
            </tr>
            <tr>
                <td width="80%">
                    <div class="addtocartholder">
                        <h3>${projectAdapter?.name}</h3>
                </td>
                <td>
                    <a href="/bardwebquery/sarCart/add/1" class="addtocart"
                       onclick="jQuery.ajax({  type:'POST',
                           data:{'id':'${projectAdapter.project.id}', 'class':'class bardqueryapi.CartProject', 'projectName':'${JavaScriptUtility.cleanup(projectAdapter.project.name)}', 'version':'0', 'stt':trackStatus},
                           url:'/bardwebquery/sarCart/add',
                           success:function (data, textStatus) {
                               jQuery(ajaxLocation).html(data);
                           }
                       });
                       return false;"
                       action="add"
                       controller="sarCart"><i class="icon-shopping-cart"></i><span
                            class="addtocartfont">&nbsp;Add to Cart</span>
                    </a>

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
                <a href="#summary-header" id="summary-header" class="accordion-toggle" data-toggle="collapse" data-target="#summary-info"><i class="icon-chevron-down"></i> Summary Info</a>
                <div id="summary-info" class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <g:render template="projectSummary" model="['projectAdapter': projectAdapter]" />
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="document-header" class="accordion-toggle" data-toggle="collapse" data-target="#document-info"><i class="icon-chevron-right"></i> Documents</a>
                <div id="document-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="projectDocuments" model="['projectAdapter': projectAdapter]" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>