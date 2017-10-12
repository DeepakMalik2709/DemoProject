import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Adapter.extend(ajaxMixin ,{
	 languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	 findRecord: function(store, type, id, snapshot) {

		    return new Ember.RSVP.Promise((resolve, reject) =>{
		      this.doGet(`/rest/calendar/calendars`).then((data)=> {
		    	  if(data.code ==0){
		    		  var record = data.item;
		    		  record.languagesUI = [];
		    		  
		    		  if(record.languages && record.languages.length){
			    		  for(var i =0; i < record.languages.length; i++){
			    			  var thisLang = record.languages[i];
			    			  for(var k =0; k< this.languages.length; k++){
			    				  var iterLang = this.languages[k];
			    				  if(iterLang.id == thisLang){
			    					  record.languagesUI.push(iterLang);
			    				  }
			    			  }
			    		  }
		    		  }
		    		  resolve(record);
		    	  }else{
		    		  reject(data);
		    	  }
		      }, function(jqXHR) {
		        reject(jqXHR);
		      });
		    });
		  }
		  
});