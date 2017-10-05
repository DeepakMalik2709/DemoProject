import DS from 'ember-data';

export default DS.Model.extend({
	dbFields : ["id","description", "eventTOs" , "header", "language", "editable", "droppable"],
	
	description: DS.attr('string'),
	editable:true,
	droppable:true,
	eventTOs: DS.attr( ),
	header:{left: 'prev,next today',
        center: 'title',
        right: 'month,agendaWeek,agendaDay'},
	languagesUI: DS.attr( {
			defaultValue() { return []; }
	}),
	  
});
