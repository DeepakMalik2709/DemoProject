import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Adapter.extend(ajaxMixin ,{
	 findRecord: function(store, type, id, snapshot) {

		    return new Ember.RSVP.Promise((resolve, reject) =>{
		      this.doGet(`/rest/public/institute/${id}`).then((data)=> {
		    	  if(data.code ==0){
		    		  var record = data.item;
		    		  var newJson = {
		    				  id: record.id,
		    			        type: 'institute',
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
			    	var url = '/rest/secure/institute/upsert';
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
			    	var url = `/rest/secure/institute/${id}`;
			    	this.doDelete(url ).then(function(data) {
			        Ember.run(null, resolve, data.item);
			      }, function(jqXHR) {
			        jqXHR.then = null; // tame jQuery's ill mannered promises
			        Ember.run(null, reject, jqXHR);
			      });
			    });
			  },
			  searchByName :  function( name) {
				    return new Ember.RSVP.Promise((resolve, reject) =>{
				    	var url = '/rest/secure/institute/search?q=' + name;
				    	this.doGet(url ).then(function(data) {
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
				  
				  joinInstitute :  function( id) {
					    return new Ember.RSVP.Promise((resolve, reject) =>{
					    	var url = '/rest/secure/institute/' + id + '/join';
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
});