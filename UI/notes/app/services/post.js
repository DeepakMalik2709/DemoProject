import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Store.extend(ajaxMixin ,{
	init() {
	    this._super(...arguments);
	  },
	 addToLibrary: function(item){
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/library/addToLibrary";
				this.doPost(url , item ).then((data ) =>{
					  resolve(data);
			      });
		 });
	  },
	 upsertPostComment: function(json) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/group/post/comment";
				this.doPost(url , json ).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
	 
	 upsertPostCommentReply: function(json) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/group/post/comment/reply";
				this.doPost(url , json ).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
	 
	 deletePost: function(groupId, postId) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/group/" + groupId + "/post/" + postId;
				this.doDelete(url  ).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
	 
	 deletePostComment: function( postId, commentId) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
			 var url = "/rest/secure/post/" + postId  + "/comment/" + commentId;
				this.doDelete(url  ).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
	 
	 deletePostCommentReply: function( postId, commentId) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
			 var url = "/rest/secure/post/" + postId  + "/comment/" + commentId;
				this.doDelete(url  ).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
	 
	 reactToPost: function( postId) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
			 var url = "/rest/secure/post/" + postId + "/react";
				this.doPost(url  ).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
	 uploadFile: function(files) {
		 var fd = new FormData();
		 for(var i=0;i<files.length;i++){
			 fd.append('file' + i, files[i]); 
		 }
	        var url = "/rest/secure/uploadFile";
	        return  new Ember.RSVP.Promise((resolve, reject) =>{
				this.doUpload(url , fd).then((data ) =>{
			    	  resolve(data);
			      });
		 });
	        
	 },
});