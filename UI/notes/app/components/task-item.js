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
    	  editTask(){
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
        
        deleteTask(){
    		this.sendAction("deleteTask", this.item);
    	},
    	//end of actions
    },
  
});