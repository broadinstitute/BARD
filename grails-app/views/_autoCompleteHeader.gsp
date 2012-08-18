<r:require modules="core"/>
<r:script>
    var projects = ${projects};
    var assays = ${assays}

    $(document).ready(function() {
       handleAutoComplete('.project',projects)
       handleAutoComplete('.assay',assays)
     //  handleAutoComplete('.cellLine',cellLines)
    });
    function handleAutoComplete(classname,listToUse){
        $(classname).autocomplete({
          source: listToUse,
          autoFocus: true,
          autoSelect: true
      });
      $(classname).live("blur", function(event) {
          var autocomplete = $(this).data("autocomplete");
          var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex($(this).val()) + "$", "i");
          var myInput = $(this);
          autocomplete.widget().children(".ui-menu-item").each(function() {
              //Check if each autocomplete item is a case-insensitive match on the input
              var item = $(this).data("item.autocomplete");
              if (matcher.test(item.label || item.value || item)) {
                  //There was a match, lets stop checking
                  autocomplete.selectedItem = item;
                  return;
              }
          });
          //if there was a match trigger the select event on that match
          //I would recommend matching the label to the input in the select event
          if (autocomplete.selectedItem) {
              autocomplete._trigger("select", event, {
                  item: autocomplete.selectedItem
              });
              //there was no match, clear the input
          } else {
              $(this).val('');
          }
      });
    }
</r:script>
