$(document).ready(function() {
var cardTemplate = "<% _.each(cardDtoList, function(cardDTO) {%>" +
		"<div class='card'>" +
		"<table class='gridtable'>" +
		"<caption> <%card.title%> </caption>" +
		"<tbody>" +
		"<% var i=0;" +
		"_.each(card.lines, function(line) {%>" +
			"<tr class='${(i++ % 2) == 0 ? \'even\' : \'odd\'}'>" +
			"<td>${line.attributeLabel}</td>" +
			"<td>${line.valueLabel}</td>" +
			"</tr> <%});%>" +
			"</tbody>" +
			"</table>" +
			"</div>" +
		"<%});%>"


// // The DOM element for a card item...
//CardItem = Backbone.View.extend({
//	
////	tagName: "cardView",
//	
//	// Cache the template function for a single item.
////    template: _.template($('#card-item-template').html()),
//	template: _.template(cardTemplate, cardDtoList),
//    
//    // The DOM events specific to an item.
//    events: {
//    	
//    },
//
//	//The TodoView listens for changes to its model, re-rendering.
//	initialize: function() {
//	  
//	},
//    
//    render: function() {
//    	
//    }
//});

// Card layout view
CardView = Backbone.View.extend({
	
	el: $("#cardView"),
	template: _.template(cardTemplate, cardDtoList),
	
	events: {
		
	},

	render: function() {
		
	},
	
	// Add a single CardItem to the list by creating a view for it
	addOne: function(todo) {
		
	}
		
});

//Card = Backbone.Model.extend({
//	
//	urlRoot : "/BARD/assayDefinition/show",
//	
//	defaults: function() {
//		
//	}
//		
//});

//CardList = Backbone.Collection.extend({
//	// Reference to this collection's model.
//    model: Card,
//    
//    // Need to add assay id to url
//    url: '/assayJSon/getCards',
//    
//    
//});
})