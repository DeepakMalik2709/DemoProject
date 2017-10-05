import Ember from 'ember';


export default Ember.Mixin.create({
	cleanupAllTutorials(myTutorials) {
        myTutorials.forEach((item1) => this.cleanupTutorial(item1));
    },
    
    doGet(targetUrl){
		return Ember.$.when(
				Ember.$.ajax({
				"url" : targetUrl,
				method : "GET",
				})
			)
	},
	doPost(targetUrl , json){
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
	
	doUpload(targetUrl , formData){
		return Ember.$.when(
				Ember.$.ajax({
				"url" : targetUrl,
				method : "POST",
				 data: formData,
        	    cache: false,
        	    contentType: false,
        	    processData: false,
				})
			)
	},
	
});