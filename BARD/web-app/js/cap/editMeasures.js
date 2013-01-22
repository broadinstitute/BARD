function enableAutoCompleteOntology (textFieldSelector, section, idFieldSelector) {
  var autoOpts = {
    minLength: 2,
    source: function( request, response ) {
      $.getJSON( 
         "/BARD/ontologyJSon/getLabelsFromTree",
            {
              label: request.term,
              tree: section
            }, 
            response
          );
      },

    select: function( event, ui ) {
      $( idFieldSelector ).val( ui.item.elementId );
    }                    
  }
  
  $( textFieldSelector ).autocomplete(autoOpts);
}

