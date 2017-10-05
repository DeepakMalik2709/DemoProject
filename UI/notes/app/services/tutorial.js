import Ember from 'ember';
import DS from 'ember-data';

export default DS.Store.extend({
	 languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	items : [],
	init: function() {
	    return this._super.apply(this, arguments);
		this.items = [];
	  },

	fetchTutorialById(id){
		for(var i = 0; i < this.items.length;i++){
			var item = this.items[i];
			if(item.id == id){
				return item;
			}
		}
		
		return new Ember.RSVP.Promise((resolve, reject) =>{
			this.get("/rest/public/tutorial/" + id).then((resp)=>{
		    	if(resp.code === 0){
		    		let tutorial = resp.item;
		    		var record = {
				            title: tutorial.title,
				            url : tutorial.url,
				            description : tutorial.description,
				            id : tutorial.id,
				            tags : tutorial.tags,
				            files : tutorial.files
				          }
		    			
		    	 		  record.languagesUI = [];
		    		  for(var i =0; i < tutorial.languages.length; i++){
		    			  var thisLang = tutorial.languages[i];
		    			  for(var k =0; k< this.languages.length; k++){
		    				  var iterLang = this.languages[k];
		    				  if(iterLang.id == thisLang){
		    					  record.languagesUI.push(iterLang);
		    				  }
		    			  }
		    		  }
			          /*const storeTutorial = this.createRecord('tutorial', record); 
			          
			          this.items.push(storeTutorial);
			          return storeTutorial;*/
			          var newJson = {
		    				  id: record.id,
		    			        type: 'group',
		    			        attributes : record,
		    		  }
		    		 // resolve(newJson);
		    		  resolve(record);
		    	  }else{
		    		  reject(resp);
		    	  }
		    });
		 });
	},
	saveTutorial(tutorial){
		tutorial.languages  = [];
	    for(var i =0;i<tutorial.languagesUI.length;i++){
	    	var lang = tutorial.languagesUI[i];
	    	tutorial.languages.push(lang.id);
	    }
	    delete  tutorial.languagesUI;
		return this.post("/rest/secure/tutorial/upsert" , tutorial);
	},
	deleteTutorial(tutorial){
		return this.doDelete("/rest/secure/tutorial/" + tutorial.id);
	},
	searchTutorial(term , nextLink) {
		var url = "/rest/public/tutorial/search?q="+ term ;
		if(nextLink){
			url = nextLink;
		}
		return this.get(url);
	},
	fetchTrending( nextLink) {
		var url = "/rest/public/trending" ;
		if(nextLink){
			url = nextLink;
		}
		return this.get(url);
	},
	fetchMyTutorialList(){
		return this.get("/rest/secure/tutorial/myList" );
	},
	
	get(targetUrl){
		return Ember.$.when(
				Ember.$.ajax({
				"url" : targetUrl,
				method : "GET",
				})
			)
	},
	post(targetUrl , json){
		return Ember.$.when(
				Ember.$.ajax({
				"url" : targetUrl,
				method : "POST",
				contentType : "application/json;charset=UTF-8",
				data : JSON.stringify(json)
				})
			)
	},
	   doDelete(targetUrl){
		return Ember.$.when(
				Ember.$.ajax({
				"url" : targetUrl,
				method : "DELETE",
				})
			)
	},
	
});