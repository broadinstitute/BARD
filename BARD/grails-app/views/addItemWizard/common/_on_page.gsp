<%
/**
 * wizard refresh flow action
 *
 * When a page (/ partial) is rendered, any DOM event handlers need to be
 * (re-)attached. The af:ajaxButton, af:ajaxSubmitJs and af:redirect tags
 * supports calling a JavaScript after the page has been rendered by passing
 * the 'afterSuccess' argument.
 *
 * Example:	af:redirect afterSuccess="onPage();"
 * 		af:redirect afterSuccess="console.log('redirecting...');"
 *
 * Generally one would expect this code to add jQuery event handlers to
 * DOM objects in the rendered page (/ partial).
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<script type="text/javascript">
		$(document).ready(function() {			
						
		})
        function onPage() {
               	outputToConsole('calling onPage() which can be used to attach generic javascript handlers to DOM elements of a rendered page / partial');
                initializePageOne();
        }

        function afterSuccess(page){
            var currentPage = page + 1;
        	outputToConsole('calling afterSuccess(page) which can be used to attach generic javascript handlers to DOM elements of a rendered page / partial');
           	outputToConsole('I am on wizard page ' + currentPage);
           	initializePageOne();
           	initializePageThree();
            if(currentPage == 5){
            	initializeFinalPage();
            }
        }

        function outputToConsole(message){
        	if (console) {
                console.log(message);
        	}
        }

        function initializePageOne(){
			
        	outputToConsole('calling initializePageOne()');

            $("#attributeId").select2({
                minimumInputLength: 2,
                width: "70%",
                placeholder: "Search for attribute name",
                query: function(query) {
                    var sectionPath = $("#sectionPath").val();
                    console.log("querying for "+query.term+" "+sectionPath)
                    $.getJSON(
                            "/BARD/ontologyJSon/getDescriptors",
                            {
                                term: query.term,
                                section: sectionPath
                            },
                            function(data, textStatus, jqXHR) {
                                console.log("got response "+data)
                                var selectData = {results:[]}
                                $.each(data, function(index, val) {
                                    selectData.results.push({id: val.elementId, text: val.label})
                                })
                                console.log("calling callback")
                                query.callback(selectData)
                            }
                    );
                }
            }).on("change", function(e) {
               $("#attributeElementId").val(e.val)
               $("#attributeLabel").val(e.text)
               outputToConsole('e.val = ' + e.val);
               outputToConsole('e.text = ' + e.text);
            })
        }



        function initializePageThree(){
            outputToConsole('calling initializePageThree()');

            $("#valueId").select2({
                minimumInputLength: 2,
                width: "70%",
                placeholder: "Search for attribute name",
                query: function(query) {
                    var cardAssaySection = $("#sectionPath").val();
                    var elementId = $("#attributeElementId").val();
                    outputToConsole('cardAssaySection var = ' + cardAssaySection);
                    outputToConsole('elementId var = ' + elementId);
                    $.getJSON(
                            "/BARD/ontologyJSon/getValueDescriptors",
                            {
                                term: query.term,
                                section: cardAssaySection,
                                attributeId: elementId
                            },
                            function(data, textStatus, jqXHR) {
                                console.log("got response "+data)
                                var selectData = {results:[]}
                                $.each(data, function(index, val) {
                                    selectData.results.push({id: val.elementId, text: val.label})
                                })
                                console.log("calling callback")
                                query.callback(selectData)
                            }
                    );
                }
            })

/*
                select: function( event, ui ) {
                    $( "#currentChoice" ).val( ui.item.value );
                    $( "#unit" ).val( ui.item.unit );
                    $( "#valueId" ).val( ui.item.elementId );
                }
*/
        }

        function initializeFinalPage(){
        	outputToConsole('calling initializeFinalPage()');

        	var assayId = $("#cardAssayId").val();
        	
        	var data = {'assayId':assayId};
        	$.ajax({
            	type:'POST',
                url:'../reloadCardHolder',
                data:data,
                success:function (data) {
                	$("div#cardHolder").html(data);
     	    	   	initDnd();
                },
                error:function (jqXHR, textStatus, errorThrown){
                	alert("Error: " + textStatus);
                }
            });
        }
</script>

