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
           	//if(currentPage == 1){
           		//initializePageOne();
            //}
            //else if(currentPage == 3){
            	//initializePageThree();
            //}
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
        	var autoOpts = {
        			minLength: 2,
        			//source: "/BARD/ontologyJSon/getDescriptors",
        			source: function( request, response ) {            			
            			var sectionPath = $("#sectionPath").val();
            			outputToConsole('Auto-complete sectionPath var = ' + sectionPath);
            			var cardAssayContextId = $("#cardAssayContextId").val();
            			outputToConsole('Auto-complete cardAssayContextId var = ' + cardAssayContextId);
            			$("#path").val(sectionPath);
            			$("#assayContextIdValue").val(cardAssayContextId);
            			outputToConsole('cardAssaySection var = ' + $("#path").val());
            			outputToConsole('assayContextId var = ' + $("#assayContextIdValue").val());
        				$.getJSON( 
                				"/BARD/ontologyJSon/getDescriptors",
                				{
                            		term: request.term,
                            		section: sectionPath
                            	}, 
                            	response 
                        );
            		},

        			select: function( event, ui ) {
                        $( "#currentValue" ).val( ui.item.value );
                        $( "#elementId" ).val( ui.item.elementId );  
                        $( "#attributeElementId" ).val(ui.item.elementId);
                        outputToConsole("Attribute Element ID: " + ui.item.elementId);           
                    }
                			
        	}
        	$( "#attributeTextField" ).autocomplete(autoOpts);
        }

        function initializePageThree(){
			
        	outputToConsole('calling initializePageThree()');
        	var autoOpts = {
        			minLength: 1,
        			source: function( request, response ) {
            			var cardAssaySection = $("#sectionPath").val();
            			var elementId = $("#attributeElementId").val();
            			outputToConsole('cardAssaySection var = ' + cardAssaySection);
            			outputToConsole('elementId var = ' + elementId);
        				$.getJSON( 
                				"/BARD/ontologyJSon/getValueDescriptors",
                				{
                            		term: request.term,
                            		section: cardAssaySection,
                            		attributeId: elementId
                            	}, 
                            	response 
                        );
            		},

        			select: function( event, ui ) {
                        $( "#currentChoice" ).val( ui.item.value );
                        $( "#unit" ).val( ui.item.unit );
                        $( "#valueId" ).val( ui.item.elementId );
                    }
                			
        	}
        	$( "#valueTextField" ).autocomplete(autoOpts);
        }

        function initializeFinalPage(){
        	outputToConsole('calling initializeFinalPage()');

        	var assayId = $("#cardAssayId").val();
        	var assayContextId = $("#cardAssayContextId").val();
        	var cardSection = $("#sectionPath").val();			      	
        	
        	$("#dialog_add_item_wizard").dialog("option", "buttons",[
        	  	{
        	    	text: "Add another item",
        	        class: "btn btn-primary",
        	        click: function(){
        	        	$( this ).dialog( "close" );
        	        	launchAddItemWizard(assayId, assayContextId, cardSection);
        	        }
        	    },				
				{
					text: "Close",
					class: "btn",
					click: function(){
						$( this ).dialog( "close" );
    				}
				}
      		]);
        	
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

