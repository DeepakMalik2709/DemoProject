import Ember from 'ember';
import postMixin from '../mixins/post';
export default Ember.Component.extend(postMixin ,{
	originalFiles : '',
	disableReactButton : false,
	recipientList : null,
	originalComment : null,
    init() {
	    this._super(...arguments);
	    this.cleanupPost(this.item);
	    this.initNewComment();
	  },
	initNewComment(){
		  this.set("recipientList" , []);
	  },
    actions: {
    	downloadSubmissions(){
    		window.open("/rest/secure/task/" + this.item.id +"/submissions/download");
    	},
        removeFile(itemToRemove) {
        	  var items = Ember.get(this.item, "files");
        	  items.removeObject(itemToRemove);
        },
        uploadFile (evt){
        	if(evt && evt.target && evt.target.files && evt.target.files.length){
			  var files = evt.target.files;
			  if(files.length + Ember.get(this.item, "files").length > 10){
				  alert("You cannot upload more than 10 files");
				  return;
			  }
			  for(var i=0;i<files.length;i++){
				  var file = files[i];
				  if(file.size > 10485760) {
					  alert("file size must be less than 10 MB.");
					  return;
				  }
			  }
			  Ember.set(this, "item.isUploading", true);
				this.get("postService").uploadFile(files).then((result)=>{
					Ember.set(this, "item.isUploading", false);
					if(result.code == 0  ){
		    			Ember.get(this.item, "files").pushObjects(result.items);
		    		}
		    	});
        	}
        },
    	updateTask(){
        	if(!Ember.get(this, "item.isUploading")){
	    		this.updateRecipients(this.item);
	    		this.sendAction("updateTask", this.item);
        	}
    	},
   	 	editClicked() {
    		this.sendAction("editTask", this.item);
        },        
        deletePost(){
    		this.sendAction("deletePost", this.item);
    	},
    	saveComment(component ){
    		if(!Ember.get(this, "isSaving") && this.postComment){
    			Ember.set(this, "isSaving", true)
    			
    		var json = {
    			comment : this.postComment,
    			recipients :  this.recipientList,
    			postId :  this.item.id
    		}
    		this.updateRecipients(json);
    		this.get("postService").upsertPostComment(json).then((result)=>{
    			Ember.set(this, "isSaving", false)
    			if(result.code == 0){
    				Ember.get(this.item,'comments').pushObject(result.item);
    				component.resetCommentBox();
    			}
    		});
			}
    	},
    	updateComment(updatedComment ){
    		if(!Ember.get(updatedComment, "isSaving") && updatedComment.comment){
    			Ember.set(updatedComment, "isSaving", true)
	    		this.get("postService").upsertPostComment(updatedComment).then((result)=>{
	    			Ember.set(updatedComment, "isSaving", false)
	    			if(result.code == 0){
	    				var commentsList = Ember.get(this.item,'comments');
	    				var index = commentsList.indexOf(updatedComment);
	    				commentsList.replace(index, 1, result.item);
	    			}
	    		});
    		}
    	},
    	//end of actions
    },
  
});