import Ember from 'ember';
import postMixin from '../mixins/post';
export default Ember.Component.extend(postMixin , {
	postComment : '',
	recipientList : null,
	 originalComment : null,
	
    init() {
	    this._super(...arguments);
	    this.cleanupPost(this.item);
	    this.set("recipientList" , []);
	  },
	
    actions: {
    	saveSubComment(component ){
    		if(!Ember.get(this, "isSaving") && this.postComment){
    			Ember.set(this, "isSaving", true)
    		let json = {
    			comment : this.postComment,
    			recipients :  this.recipientList,
    			postId :  this.item.postId,
    			commentId : this.item.commentId
    		}
    		this.updateRecipients(json);
    		this.get("postService").upsertPostCommentReply(json).then((result)=>{
    			Ember.set(this, "isSaving", false)
    			if(result.code == 0){
    				Ember.get(this.item,'comments').pushObject(result.item);
    				component.resetCommentBox();
    			}
    		});
    		}
    	},
    	updateCommentReply(updatedCommentReply ){
    		if(!Ember.get(updatedCommentReply, "isSaving") && updatedCommentReply.comment){
    			Ember.set(updatedCommentReply, "isSaving", true)
    		this.get("postService").upsertPostCommentReply(updatedCommentReply).then((result)=>{
    			Ember.set(updatedCommentReply, "isSaving", false)
    			if(result.code == 0){
    				var commentsList = Ember.get(this.item,'comments');
    				var index = commentsList.indexOf(updatedCommentReply);
    				commentsList.replace(index, 1, result.item);
    			}
    		});
    		}
    	},
    	updateComment(){
    		this.updateRecipients(this.item);
    		this.sendAction("update", this.item);
    	},
    	editComment(){
        	this.originalComment = Ember.get(this.item, "comment");
        	Ember.set(this,"item.isEditing", true);
        	 this.$(".edit-comment").html(this.originalComment);
        },
        cancelEditing(){
        	Ember.set(this,"item.isEditing", false);
        	Ember.set(this, "item.comment", this.originalComment);
        },
    	deleteCommentReply(commentReply){
            let confirmation = confirm("Are you sure you want to delete comment ?");

            if (confirmation) {
    			Ember.get(this.item,'comments').removeObject(commentReply);
    			this.get("postService").deletePostCommentReply( Ember.get(commentReply, "postId"), Ember.get(commentReply,"subCommentId") ).then((result)=>{
            		if(result.code == 0){
    	    		}
    	    	});
            }
    	},
        deleteComment( ){
        	this.sendAction("deleteComment", this.item);
    	}
    }
});