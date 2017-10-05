import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../../mixins/ajax';

export default DS.Store.extend(ajaxMixin,{
	 languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	items : [],
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
	}
});