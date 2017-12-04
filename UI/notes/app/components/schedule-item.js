import Ember from 'ember';
import postMixin from '../mixins/post';
export default Ember.Component.extend(postMixin ,{
	postComment : '',
	originalFiles : '',
	disableReactButton : false,
	recipientList : null,
	originalComment : null,	
	isCreator : true,
	postRecipient :null,
	replyBtn:'default',
	isAccepted:false,
	isTentative:false,
	isDeclined:false,

	scheduleService: Ember.inject.service('new.schedule'),
    init() {
	    this._super(...arguments);
	    this.cleanupPost(this.item);
	    this.initScheduleResponse();
	    this.initNewComment();
	  },
	  initScheduleResponse(){
			this.set('isAccepted',  false);
			this.set('isTentative', false);
			this.set('isDeclined',  false);
		  if(this.get('item.postPriv') =='creator'){
				this.set('isCreator',  true);
			}else{
				this.set('isCreator',  false);
				if(this.get('item.recipients').length>0){
					var response = this.get('item.recipients')[0];
					this.set('postRecipient',response);
					if(response.scheduleResponse == 'ACCEPTED'){
						this.set('isAccepted',  true);
					}else if(response.scheduleResponse == 'TENTATIVE'){
						this.set('isTentative',  true);
					}else{
						this.set('isDeclined',  true);
					}
				}
			}
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
    	updateRecipientRes(response){
    		var json = {   
    				id : this.get('postRecipient.id'),
        			scheduleResponse : response,
        			postId :  this.item.id
        		}
    		this.get("scheduleService").updateRecipientRes(json).then((result)=>{    			
    			if(result.code == 0){
    				var response =[];
    				response.push(result.item);
    				this.set('item.recipients',response)
    				this.initScheduleResponse();    				
    			}
    		});
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
    	}
    	
    	//end of actions
    },
  
});