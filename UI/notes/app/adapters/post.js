import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Adapter.extend(ajaxMixin ,{
	 languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	 findRecord: function(store, type, id, snapshot) {

		    return new Ember.RSVP.Promise((resolve, reject) =>{
		      this.doGet(`/rest/secure/post/${id}`).then((data)=> {
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
		    		  var newJson = {
		    				  id: record.id,
		    			        type: 'post',
		    			        attributes : record,
		    		  }
		    		  resolve(record);
		    	  }else{
		    		  reject(data);
		    	  }
		      }, function(jqXHR) {
		        reject(jqXHR);
		      });
		    });
		  },
		  
		 createRecord: function(store, type, snapshot) {
			 return this.upsert(store, type, snapshot);
		 },
		  updateRecord: function(store, type, snapshot) {
				 return this.upsert(store, type, snapshot);
			 },
		  
		  upsert :  function(store, type, snapshot) {
			    var json = this.serialize(snapshot, { includeId: true });
			    return new Ember.RSVP.Promise((resolve, reject) =>{
			    	var url = '/rest/secure/group/post';
			    	this.doPost(url , json).then(function(data) {
			    	  Ember.run(null, resolve, data.item);
			      }, function(jqXHR) {
			        jqXHR.then = null; // tame jQuery's ill mannered promises
			        Ember.run(null, reject, jqXHR);
			      });
			    });
			  },
		  
		  deleteRecord: function(store, type, snapshot) {
			    var id = snapshot.id;
			    var post = this.serialize(snapshot, { includeId: true });
			    return new Ember.RSVP.Promise((resolve, reject) =>{
			    	var url = "/rest/secure/group/" + post.groupId + "/post/" + post.id;
			    	this.doDelete(url ).then(function(data) {
			        Ember.run(null, resolve, data.item);
			      }, function(jqXHR) {
			        jqXHR.then = null; // tame jQuery's ill mannered promises
			        Ember.run(null, reject, jqXHR);
			      });
			    });
			  },
			  saveTask :  function( snapshot) {
				    var json = this.serialize(snapshot, { includeId: true });
				    return new Ember.RSVP.Promise((resolve, reject) =>{
				    	var url = '/rest/secure/group/task';
				    	this.doPost(url , json).then(function(data) {
				    		if(data.code == 0){
				    			Ember.run(null, resolve, data.items[0]);
				    		}else{
				    			  Ember.run(null, reject, data);
				    		}
				      }, function(jqXHR) {
				        jqXHR.then = null; // tame jQuery's ill mannered promises
				        Ember.run(null, reject, jqXHR);
				      });
				    });
				  },
				  saveSchedule :  function( snapshot) {
					    var json = this.serialize(snapshot, { includeId: true });
					    return new Ember.RSVP.Promise((resolve, reject) =>{
					   	var url = '/rest/calendar/insertEvent';
					   	this.doPost(url , json).then(function(data) {
				   			if(data.code == 0){
				    			Ember.run(null, resolve,data.items[0]);
				    		}else{
				    			  Ember.run(null, reject, data);
				    		}
					     }, function(jqXHR) {
					        jqXHR.then = null; // tame jQuery's ill mannered promises
					        Ember.run(null, reject, jqXHR);
					      });
					    });
					}
});