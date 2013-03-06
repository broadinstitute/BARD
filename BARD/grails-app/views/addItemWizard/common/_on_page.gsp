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
               	var pageNumber = $("#pageNumber").val();
            	outputToConsole('Form page number: ' + pageNumber);
                initializePageOne();
        }

        function afterSuccess(){
        	outputToConsole('calling afterSuccess()');
        	var pageNumber = $("#pageNumber").val();
        	outputToConsole('I am on wizard page number: ' + pageNumber);
           	if(pageNumber == 1){
           		initializePageOne();
            }           	
           	if(pageNumber == 3){
           		var valueType = $("#valueType").val();
           		outputToConsole('valueType =' + valueType);
               	if(valueType && valueType == 'Fixed')
           			initializePageThreeFixed();
               	else if(valueType && valueType == 'List')
               		initializePageThreeList();
           	}
            if(pageNumber == 5){
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

        	var attribIdCache = {}
            $("#attributeId").select2({
                minimumInputLength: 2,
                width: "70%",
                allowClear: true,
                placeholder: "Search for attribute name",
                query: function(query) {
                	var sectionPath = $("#sectionPath").val();
                    $.getJSON(
                            "/BARD/ontologyJSon/getDescriptors",
                            {
                                term: query.term,
                                section: sectionPath
                            },
                            function(data, textStatus, jqXHR) {
                                var selectData = {results:[]}
                                $.each(data, function(index, val) {
                                    selectData.results.push({id: val.elementId, text: val.label})
                                    attribIdCache[val.elementId] = val.unitId;
                                })
                                query.callback(selectData)
                            }
                    );
                }
            }).on("change", function(e) {
               $("#attributeElementId").val(e.val);
               $("#attributeLabel").val(e.text);
               $("#attributeElementUnitId").val(attribIdCache[e.val]);
               outputToConsole('e.val = ' + e.val);
               outputToConsole('attributeElementId = ' + $("#attributeElementId").val());
               outputToConsole('attributeElementUnitId = ' + attribIdCache[e.val]);
            })
        }



        function initializePageThreeFixed(){
            outputToConsole('calling initializePageThreeFixed()');

            $("#valueId").select2({
                minimumInputLength: 1,
                width: "70%",
                allowClear: true,
                placeholder: "Search for value name",
                query: function(query) {
                	var cardAssaySection = $("#sectionPath").val();
                    var elementId = $("#attributeElementId").val();
                    //outputToConsole('cardAssaySection var = ' + cardAssaySection);
                    outputToConsole('elementId var = ' + elementId);
                    $.getJSON(
                            "/BARD/ontologyJSon/getValueDescriptors",
                            {
                                term: query.term,
                                section: cardAssaySection,
                                attributeId: elementId
                            },
                            function(data, textStatus, jqXHR) {
                                var selectData = {results:[]}
                                $.each(data, function(index, val) {
                                    selectData.results.push({id: val.elementId, text: val.label})
                                })
                                query.callback(selectData)
                            }
                    );
                }
            })

            var attributeElementId = $("#attributeElementId").val();
            var attributeElementUnitId = $("#attributeElementUnitId").val();
            var unitsData = {results:[]};
            if($("#valueUnitId")){
            	outputToConsole('valueUnitId found');
            }
            else{
            	outputToConsole('valueUnitId NOT found');
            }
            if(attributeElementUnitId){
            	$.getJSON(
                    	"/BARD/ontologyJSon/getBaseUnits",
                        {
                            elementId: attributeElementId,
                            toUnitId: attributeElementUnitId
                        },
                        function(data, textStatus, jqXHR) {                    	
                            $.each(data, function(index, val) {
                            	unitsData.results.push({id: val.value, text: val.label})
                           	});
                            $("#valueUnitId").select2({
                            	placeholder: "Select a Unit",
                           		width: "70%",
                                data: unitsData
                    		})
                        }
                );      
            }
            else{
            	outputToConsole('/BARD/ontologyJSon/getAllUnits request sent');
            	$.getJSON(
                    	"/BARD/ontologyJSon/getAllUnits",
                        function(data, textStatus, jqXHR) {
                    		outputToConsole('/BARD/ontologyJSon/getAllUnits data received');
                            $.each(data, function(index, val) {
                            	unitsData.results.push({id: val.value, text: val.label})
                           	})
                           	outputToConsole('/BARD/ontologyJSon/getAllUnits data processed');
                           	$("#valueUnitId").select2({
                           		placeholder: "Select a Unit",
                           		width: "70%",
        		                data: unitsData
                    		})
                        }
                );     
            }
            

            $("#valueUnitId").select2({
           		placeholder: "Loading units..",
           		width: "70%",
                data: unitsData
    		})
    		
        }

        function initializePageThreeList(){
            outputToConsole('calling initializePageThreeList()');

            $("#valueId").select2({
                minimumInputLength: 1,
                width: "70%",
                allowClear: true,
                placeholder: "Search for value name",
                query: function(query) {
                	var cardAssaySection = $("#sectionPath").val();
                    var elementId = $("#attributeElementId").val();
                    //outputToConsole('cardAssaySection var = ' + cardAssaySection);
                    outputToConsole('elementId var = ' + elementId);
                    $.getJSON(
                            "/BARD/ontologyJSon/getValueDescriptors",
                            {
                                term: query.term,
                                section: cardAssaySection,
                                attributeId: elementId
                            },
                            function(data, textStatus, jqXHR) {
                                var selectData = {results:[]}
                                $.each(data, function(index, val) {
                                    selectData.results.push({id: val.elementId, text: val.label})
                                })
                                query.callback(selectData)
                            }
                    );
                }
            })

            var attributeElementId = $("#attributeElementId").val();
            var attributeElementUnitId = $("#attributeElementUnitId").val();
            var unitsData = {results:[]};
            $("#valueUnitId").select2({
           		placeholder: "Loading units..",
           		width: "70%",
                data: unitsData
    		})
            
            if(attributeElementUnitId){
            	outputToConsole('/BARD/ontologyJSon/getBaseUnits request sent');
            	var unitLabelCache = {}
            	$.getJSON(
                    	"/BARD/ontologyJSon/getBaseUnits",
                        {
                            elementId: attributeElementId,
                            toUnitId: attributeElementUnitId
                        },
                        function(data, textStatus, jqXHR) {                    	
                            $.each(data, function(index, val) {
                            	unitsData.results.push({id: val.value, text: val.label})
                            	unitLabelCache[val.value] = val.label;
                           	});
                            $("#valueUnitId").select2({
                            	placeholder: "Select a Unit",
                           		width: "70%",
                                data: unitsData
                    		}).on("change", function(e) {
                                $("#valueUnitLabel").val(unitLabelCache[e.val])                               
                            })
                        }
                );      
            }
            else{
            	outputToConsole('/BARD/ontologyJSon/getAllUnits request sent');
            	var unitLabelCache = {}
            	$.getJSON(
                    	"/BARD/ontologyJSon/getAllUnits",
                        function(data, textStatus, jqXHR) {
                    		outputToConsole('/BARD/ontologyJSon/getAllUnits data received');
                            $.each(data, function(index, val) {
                            	unitsData.results.push({id: val.value, text: val.label})
                            	unitLabelCache[val.value] = val.label;
                           	})
                           	outputToConsole('/BARD/ontologyJSon/getAllUnits data processed');
                           	$("#valueUnitId").select2({
                           		placeholder: "Select a Unit",
                           		width: "70%",
        		                data: unitsData
                    		}).on("change", function(e) {
                                $("#valueUnitLabel").val(unitLabelCache[e.val])                               
                            })
                        }
                );     
            }
            

            
    		
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

