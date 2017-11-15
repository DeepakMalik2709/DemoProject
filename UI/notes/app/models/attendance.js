import DS from 'ember-data';

export default DS.Model.extend({
	dbFields : ["id","title", "start" , "end", "language", "description", "eventType","backgroundColor","borderColor","location"],
	
	title: DS.attr(""),
	location:DS.attr(""),
	start:DS.attr('date',{defaultValue() { return new Date(); }}),
	end:DS.attr('date',{defaultValue() { return new Date(); }}),
	description: DS.attr('string'),
	eventType:DS.attr('string'),
	backgroundColor:DS.attr('string'),
	borderColor:DS.attr('string'),
	
	attendees:DS.attr('string'),
	
	groups:DS.attr( ),
	groupId: DS.attr(""),
	  comment: DS.attr(""),
	  createdByEmail: DS.attr(""),
	  createdByName: DS.attr(""),
	  updatedByEmail: DS.attr(""),
	  updatedByName: DS.attr(""),
	  createdTime: DS.attr(""),
	  updatedTime: DS.attr(""),
	  numberOfReactions: DS.attr(""),
	 comments : DS.attr(""),
	  files : DS.attr(""),
	
	
	
	languagesUI: DS.attr( {
			defaultValue() { return []; }
	})
	
	  
});
