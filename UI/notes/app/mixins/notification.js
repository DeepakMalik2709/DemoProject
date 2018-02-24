import Ember from 'ember';

export default Ember.Mixin.create({
	
	 listenComments: function() {
		 var controller = this.controller;
		 $(document).on("newComment", function(event , notification ){
	        	var posts = controller.get("posts");
	        	if(posts){
		        	 for(var i =0;i<posts.length;i++){
		        		 var post = posts[i];
		        		 if(post.id ==notification.entityId){
		        			 if("COMMENT" == notification.type){
		        				 Ember.get(post, "comments").pushObject(notification.item)
		            		 }else if("COMMENT_REPLY" == notification.type){
		            			 var thisComment = Ember.get(post, "comments").filterBy("commentId", notification.item.commentId)[0] ;
		            			 if(thisComment){
		            				 Ember.get(thisComment, "comments").pushObject(notification.item);
		            			 }
		            		 }
		        			 break;
		        		 }
		        	}
	        	}else{
	        		var post = controller.get("model");
	        		 if(post && post.id ==notification.entityId){
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
			  });
		  },

		  unlistenComments: function() {
			  $(document).off("newComment");
		  },
});