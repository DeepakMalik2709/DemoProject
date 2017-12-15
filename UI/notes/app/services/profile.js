import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Store.extend(ajaxMixin ,{
		myGroups : null,
		init() {
		    this._super(...arguments);
		  },
		  
	 removePhoto: function() {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/profile/photo";
				this.doDelete(url).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
	 saveInstitutesInformation: function(json) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
			 var url = "/rest/secure/instituteMembers/save";
			 this.doPost(url , json).then((data ) =>{
				  resolve(data);
		      });
		 });
	 },
	 uploadPhoto: function(file) {
		 var fd = new FormData();
	        fd.append('file', file);
	        
	        var url = "/rest/secure/profile/photo";
	        return  new Ember.RSVP.Promise((resolve, reject) =>{
				this.doUpload(url , fd).then((data ) =>{
			    	  resolve(data);
			      });
		 });
	        
	 },
	 deauthorizeGoogleDrive : function(){
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/profile/deauthorizeGoogleDrive";
				this.doPost(url).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
	 deauthorizeGoogleCalendar : function(){
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/profile/deauthorizeGoogleCalendar";
				this.doPost(url).then((data ) =>{
					  resolve(data);
			      });
		 });
	 }
});