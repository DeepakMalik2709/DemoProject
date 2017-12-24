import Ember from 'ember';
import postMixin from '../mixins/post';
export default Ember.Component.extend(postMixin ,{
	postComment : '',
	originalFiles : '',
	disableReactButton : false,
	recipientList : null,
	originalComment : null,
	useGoogleDrive : false,
    init() {
	    this._super(...arguments);
	    this.cleanupPost(this.item);
	    this.initNewComment();
	    this.useGoogleDrive = Ember.get(this.get("contextService").fetchContext().get("loginUser"), "useGoogleDrive");
	  },
	initNewComment(){
		  this.set("recipientList" , []);
	  },
    actions: {
    
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
		/*	  for(var i=0;i<files.length;i++){
				  var file = files[i];
				  if(file.size > 10485760) {
					  alert("file size must be less than 10 MB.");
					  return;
				  }
			  }*/
			  Ember.set(this, "item.isUploading", true);
				this.get("postService").uploadFile(files).then((result)=>{
					Ember.set(this, "item.isUploading", false);
					if(result.code == 0  ){
		    			Ember.get(this.item, "files").pushObjects(result.items);
		    			console.log(Ember.get(this.item, "files").length)
		    		}
		    	});
        	}
        },
    	updatePost(){
        	if(!Ember.get(this, "item.isUploading")){
	    		this.updateRecipients(this.item);
	    		this.sendAction("updatePost", this.item);
        	}
    	},
    	  editPost(){
        	this.originalComment = Ember.get(this.item, "comment");
        	this.originalFiles = Ember.copy(Ember.get(this.item,"files"));
        	Ember.set(this,"item.isEditing", true);
        	 this.$(".edit-post").html(this.originalComment);
        },
        cancelEditing(){
        	Ember.set(this,"item.isEditing", false);
        	Ember.set(this.item, "comment", this.originalComment);
        	Ember.set(this.item, "files", this.originalFiles);
        },
        deletePost(){
    		this.sendAction("deletePost", this.item);
    	},
    	deleteComment(comment){
            let confirmation = confirm("Are you sure you want to delete comment ?");

            if (confirmation) {
    			Ember.get(this.item,'comments').removeObject(comment);
    			this.get("postService").deletePostComment( Ember.get(comment, "postId"), Ember.get(comment,"commentId") ).then((result)=>{
            		if(result.code == 0){
    	    		}
    	    	});
            }
    	},
    	
    	reactToPost(){
    		Ember.set(this, "disableReactButton", true);
    		this.get("postService").reactToPost( Ember.get(this.item, "id")).then((result)=>{
    			Ember.set(this, "disableReactButton", false);
    			console.log(result);
        		if(result.code == 0){
        			Ember.set(this.item, "numberOfReactions", result.item);
	    		}
	    	});
    	},
    	 attachFilesClick(evt){
    	    	if(this.useGoogleDrive){
    	    		 this.$('menu').find('.file-upload').click();
    				}else{
    					if(confirm("You must grant AllSchool permission to save your files to Google Drive to use this feature.")){
    	     				window.location.href= "/a/oauth/googleAllAuthorization";
    	     			}
    				}
    	    	
    	    }
    	//end of actions
    },
   
  
});