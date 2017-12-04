import DS from 'ember-data';
import ajaxMixin from '../../mixins/ajax';

export default DS.Adapter.extend(ajaxMixin ,{
	languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	createRecord: function(store, type, snapshot) {
		return this.upsert(store, type, snapshot);
	},
	updateRecord: function(store, type, snapshot) {
		return this.upsert(store, type, snapshot);
	}, 
	upsert :  function(store, type, snapshot) {
	    var json = this.serialize(snapshot, { includeId: true });
	    return new Ember.RSVP.Promise((resolve, reject) =>{
	   	var url = '/rest/calendar/insertEvent';
	   	this.doPost(url , json).then(function(data) {
	   		if(data.code ==0){
	   			var records={
	   					id:'schedules',
	   					data:data.items
	   			}
	   			Ember.run(null, resolve, records);
	   		}else{
	   			Ember.run(null, reject, jqXHR);
	    	}	   		
	     }, function(jqXHR) {
	        jqXHR.then = null; // tame jQuery's ill mannered promises
	        Ember.run(null, reject, jqXHR);
	      });
	    });
	}
		  
});