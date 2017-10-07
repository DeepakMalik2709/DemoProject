import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Store.extend(ajaxMixin,{
	 languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	items : [],
	init() {
		 this._super(...arguments);
		    this.set('items', []);
	  },
	  updateResponse: function(json) {
			 return  new Ember.RSVP.Promise((resolve, reject) =>{
					var url = "/rest/calendar/updateEvent";
					this.doPost(url , json ).then((data ) =>{
						  resolve(data);
				      });
			 });
		 },

	saveEvent:function(event){
		event.languages  = [];
	    for(var i =0;i<event.languagesUI.length;i++){
	    	var lang = event.languagesUI[i];
	    	event.languages.push(lang.id);
	    }
	    delete  event.languagesUI;
		return this.post("/rest/secure/event/upsert" , event);
	},
	deleteEvent:function(event){
		return this.doDelete("/rest/secure/event/" + event.id);
	},
	searchEvent:function(term , nextLink) {
		var url = "/rest/public/event/search?q="+ term ;
		if(nextLink){
			url = nextLink;
		}
		return this.get(url);
	},
	fetchTrending:function( nextLink) {
		var url = "/rest/public/trending" ;
		if(nextLink){
			url = nextLink;
		}
		return this.get(url);
	},
	fetchMyEventList:function(){
		return this.get("/rest/secure/calendar/event" );
	}
	
});