import DS from 'ember-data';

export default DS.Model.extend({
	dbFields : ["id","title", "start" , "end", "language", "description", "eventType","backgroundColor","borderColor","location"],
	
	title: DS.attr('string'),
	start:DS.attr('number'),
	end:DS.attr('number'),
	description: DS.attr('string'),
	eventType:DS.attr('string'),
	backgroundColor:DS.attr('string'),
	borderColor:DS.attr('string'),
	location:DS.attr('string'),
	attendees:DS.attr('string'),
	languagesUI: DS.attr( {
			defaultValue() { return []; }
	})
	
	  
});
