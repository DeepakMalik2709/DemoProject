import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Adapter.extend(ajaxMixin ,{
	 languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	 findRecord: function(store, type, id, snapshot) {

		    return new Ember.RSVP.Promise((resolve, reject) =>{
		      this.doGet(`/rest/secure/group/${id}`).then((data)=> {
		    	  if(data.code ==0){
		    		  var record = data.item;
		    		  record.languagesUI = [];
		    		  for(var i =0; i < record.languages.length; i++){
		    			  var thisLang = record.languages[i];
		    			  for(var k =0; k< this.languages.length; k++){
		    				  var iterLang = this.languages[k];
		    				  if(iterLang.id == thisLang){
		    					  record.languagesUI.push(iterLang);
		    				  }
		    			  }
		    		  }
		    		  var newJson = {
		    				  id: record.id,
		    			        type: 'group',
		    			        attributes : record,
		    		  }
		    		 // resolve(newJson);
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
			    json.languages  = [];
			    for(var i =0;i<json.languagesUI.length;i++){
			    	var lang = json.languagesUI[i];
			    	json.languages .push(lang.id);
			    }
			    delete  json.languagesUI;
			    return new Ember.RSVP.Promise((resolve, reject) =>{
			    	var url = '/rest/secure/group/upsert';
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
			    return new Ember.RSVP.Promise((resolve, reject) =>{
			    	var url = `/rest/secure/group/${id}`;
			    	this.doDelete(url ).then(function(data) {
			        Ember.run(null, resolve, data.item);
			      }, function(jqXHR) {
			        jqXHR.then = null; // tame jQuery's ill mannered promises
			        Ember.run(null, reject, jqXHR);
			      });
			    });
			  },
			  findPublicRecord: function(id) {
				    return new Ember.RSVP.Promise((resolve, reject) =>{
				      this.doGet(`/rest/public/group/${id}`).then((data)=> {
				    	  if(data.code == 0){
				    		  let storePost1 = this.store.peekRecord('group', data.item.id);
								if(storePost1){
									this.store.unloadRecord(storePost1);
								}
								 
				    		  const storeGroup = this.store.createRecord('group', data.item);
				    			Ember.run(null, resolve, storeGroup);
				    		}else{
				    			  Ember.run(null, reject, jqXHR);
				    		}
				    	  
				      }, function(jqXHR) {
				        reject(jqXHR);
				      });
				    });
				  },
				  joinGroup :  function( id) {
					    return new Ember.RSVP.Promise((resolve, reject) =>{
					    	var url = '/rest/secure/group/' + id + '/join';
					    	this.doPost(url ).then(function(data) {
					    		if(data.code == 0){
					    			Ember.run(null, resolve, data.items);
					    		}else{
					    			  Ember.run(null, reject, jqXHR);
					    		}
					      }, function(jqXHR) {
					        jqXHR.then = null; // tame jQuery's ill mannered promises
					        Ember.run(null, reject, jqXHR);
					      });
					    });
					  },
					  approveMember :  function( id, member) {
						    return new Ember.RSVP.Promise((resolve, reject) =>{
						    	let url = "/rest/secure/group/" + id + "/approveJoin";
						    	let json = {
					        			email : member.email,
					        			positions : [],
					        			id:member.id,
					        	}
					        	for(var i =0; i<member.roles.length;i++){
					        		var role = member.roles[i];
					        		json.positions.push(role.id);
					        	}
						    	this.doPost(url, json ).then(function(data) {
						    		if(data.code == 0){
						    			Ember.run(null, resolve, data);
						    		}else{
						    			  Ember.run(null, reject, jqXHR);
						    		}
						      }, function(jqXHR) {
						        jqXHR.then = null; // tame jQuery's ill mannered promises
						        Ember.run(null, reject, jqXHR);
						      });
						    });
						  },
						  fetchChildGroups :  function( groupId) {
							    return new Ember.RSVP.Promise((resolve, reject) =>{
							    	var url = '/rest/public/group/' + groupId + '/children' ;
							    	this.doGet(url ).then(function(data) {
							    		if(data){
							    			Ember.run(null, resolve, data);
							    		}else{
							    			  Ember.run(null, reject, jqXHR);
							    		}
							      }, function(jqXHR) {
							        jqXHR.then = null; // tame jQuery's ill mannered promises
							        Ember.run(null, reject, jqXHR);
							      });
							    });
							  },
});