import Ember from 'ember';

export default Ember.Component.extend({
	postService: Ember.inject.service('post'),
    actions: {

    	 uploadFile (evt){
	           	if(evt && evt.target && evt.target.files && evt.target.files.length){
	   			  var files = evt.target.files;
	   			  
	   			  if(files.length > 10){
	   				  alert("You cannot upload more than 10 files");
	   				  return;
	   			  }
	   			  if(!this.useGoogleDrive){
	   				  for(var i=0;i<files.length;i++){
	   					  var file = files[i];
	   					  if(file.size > 10485760) {
	   						  alert("file size must be less than 10 MB.");
	   						  return;
	   					  }
	   				  }
	   			  }
	   			  Ember.set(this.item, "showLoading", true);
	   			  Ember.set(this.item, "isSaving", true);
	   				this.get("postService").uploadFile(files).then((result)=>{
	   					  Ember.set(this.item, "showLoading", false);
	   					  Ember.set(this.item, "isSaving", false);
	   		    		if(result.code == 0  ){
	   		    			Ember.get(this, "files").pushObjects(result.items);
	   		    		}
	   		    	});
	           	}
	           },
	           
	           removeFile(itemToRemove) {
	           	  var items = Ember.get(this, "files");
	           	  items.removeObject(itemToRemove);
	           },
    }
});