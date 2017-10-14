import Ember from 'ember';
import postMixin from '../mixins/post';
export default Ember.Component.extend(postMixin , {
	 taskService :  Ember.inject.service('task'),
    init() {
	    this._super(...arguments);
	    this.set("files" , []);
	    this.set("postComment" , "");
	    this.set("showTaskSubmitOptions" , false);
	  },
	
    actions: {
    	saveTaskSubmission( ){
    		if(!Ember.get(this, "isSaving") && this.files.length){
    			Ember.set(this, "isSaving", true)
    			
    			console.log(this.task.id);
    		let json = {
    			comment : this.postComment,
    			files :  this.files,
    			postId :  this.task.id,
    		}
    			console.log(11 , json);
    		this.get("taskService").saveTaskSubmission(json).then((result)=>{
    			Ember.set(this, "isSaving", false)
    			if(result.code == 0){
    				Ember.set(this.task , "isSubmitted" , true);
    				alert("Your submission has been saved");
    				//Ember.get(this.item,'comments').pushObject(result.item);
    			}
    		});
    		}
    	},

    	
    	 cancelTaskSubmission(submission){
    		 this.set("files" , []);
    		    this.set("postComment" , "");
        },
    
        removeFile(itemToRemove) {
        	  var items = Ember.get(this, "files");
        	  items.removeObject(itemToRemove);
        },
        uploadFile (evt){
        	if(evt && evt.target && evt.target.files && evt.target.files.length){
  			  var files = evt.target.files;
  			  if(files.length + Ember.get(this, "files").length > 10){
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
  			  Ember.set(this, "isUploading", true);
  				this.get("postService").uploadFile(files).then((result)=>{
  					Ember.set(this, "isUploading", false);
  					if(result.code == 0  ){
  		    			Ember.get(this, "files").pushObjects(result.items);
  		    		}
  		    	});
        	}
        },
        showTaskSubmitOptionsClick(){
   		 this.set("showTaskSubmitOptions" , true);
   	},
        showFileUpload(){
        	this.$('.file-upload-task').click();
        }
    }
});