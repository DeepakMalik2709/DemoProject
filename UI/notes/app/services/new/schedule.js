import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../../mixins/ajax';

export default DS.Store.extend(ajaxMixin,{
	languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	init() {
		 this._super(...arguments);
		   
	  },

	saveRecord:function(event){
		event.languages  = [];
	    for(var i =0;i<event.languagesUI.length;i++){
	    	var lang = event.languagesUI[i];
	    	event.languages.push(lang.id);
	    }
	    delete  event.languagesUI;
		return this.post("/rest/secure/event/upsert" , event);
	},
	updateRecipientRes:function(json){		
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/calendar/scheduleResponse";
				this.doPost(url,json).then((data ) =>{
			    	  resolve(data);
			      });
		 });
	},
	fetchtodayScheduleCount:function(){
		return  new Ember.RSVP.Promise((resolve, reject) =>{
			var url = "/rest/calendar/todayScheduleCount";
			this.doGet(url).then((data ) =>{
		    	  resolve(data);
		      });
	 });
	}
});