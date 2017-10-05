import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Adapter.extend(ajaxMixin ,{
	 findRecord: function(store, type, id, snapshot) {

		    return new Ember.RSVP.Promise((resolve, reject) =>{
		      this.doGet(`/rest/secure/tags/${id}`).then((data)=> {
		    	  if(data.code ==0){
		    		  var record = data.item;
		    		  var newJson = {
		    				  id: record.id,
		    			        type: 'tag',
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
			    return new Ember.RSVP.Promise((resolve, reject) =>{
			    	var url = '/rest/secure/tags/upsert';
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
			    	var url = `/rest/secure/tags/${id}`;
			    	this.doDelete(url ).then(function(data) {
			        Ember.run(null, resolve, data.item);
			      }, function(jqXHR) {
			        jqXHR.then = null; // tame jQuery's ill mannered promises
			        Ember.run(null, reject, jqXHR);
			      });
			    });
			  }
});