import Ember from 'ember';

export default Ember.Mixin.create({
	
	 listenComments: function() {
		 var controller = this.controller;
		 
		 var  updateCommentInPost = function(post , notification){
			 if(post && notification){
				 if("COMMENT" == notification.type){
    				 Ember.get(post, "comments").pushObject(notification.item)
        		 }else if("COMMENT_REPLY" == notification.type){
        			 var thisComment = Ember.get(post, "comments").filterBy("commentId", notification.item.commentId)[0] ;
        			 if(thisComment){
        				 Ember.get(thisComment, "comments").pushObject(notification.item);
        			 }
        		 }
			 }
		 }
		 
		 $(document).on("newComment", function(event , notification ){
	        	var posts = controller.get("posts");
	        	if(posts){
		        	 for(var i =0;i<posts.length;i++){
		        		 var post = posts[i];
		        		 if(post.id ==notification.entityId){
		        			 updateCommentInPost(post, notification);
		        			 break;
		        		 }
		        	}
	        	}else{
	        		var post = controller.get("model");
	        		 if(post && post.id ==notification.entityId){
	        			 updateCommentInPost(post, notification);
	        		 }
	        	}
			  });
		 
		 var  updateFilesInPost = function(post , updatedFiles){
			 if(post && updatedFiles && updatedFiles.length){
				 var files = Ember.get(post, "files");
	    			if(files){
	    				files.clear();
	    				files.pushObjects(updatedFiles);
	    			}else{
	    				Ember.set(post, "files", updatedFiles);
	    			}
	    			
			 }
			 
		 }
		 
		 $(document).on("postUpdate", function(event , updatedPost ){
	        	var posts = controller.get("posts");
	        	if(posts){
		        	 for(var i =0;i<posts.length;i++){
		        		 var post = posts[i];
		        		 if(post.id == updatedPost.id){
		        			 updateFilesInPost(post, updatedPost.files)
		        			 break;
		        		 }
		        	}
	        	}else{
	        		var post = controller.get("model");
	        		updateFilesInPost(post, updatedPost.files);
	        	}
			  });
		 
		  },

		  unlistenComments: function() {
			  $(document).off("newComment");
			  $(document).off("postUpdate");
		  },
});