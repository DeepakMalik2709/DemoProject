import Ember from 'ember';

export default Ember.Component.extend({
	contextService: Ember.inject.service('context'),
	postService: Ember.inject.service('post'),
	useGoogleDrive : false,
	 init() {
		    this._super(...arguments);
		    this.useGoogleDrive = Ember.get(this.get("contextService").fetchContext().get("loginUser"), "useGoogleDrive");
		    this.set("controllerRef" , this.controllerRef);
		  },
    actions: {
        savePost(param) {
           this.sendAction('savePost', param);
        },

        cancelClicked(param) {
            this.sendAction('cancelClicked', param);
        },
        removeFile(itemToRemove) {
      	  var items = Ember.get(this.item, "files");
      	  items.removeObject(itemToRemove);
      },
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
		    			Ember.get(this.item, "files").pushObjects(result.items);
		    		}
		    	});
        	}
        },
        attachFilesClick(evt){
        	if(this.useGoogleDrive){
					$('#file-upload').click();
				}else{
					if(confirm("You must grant AllSchool permission to save your files to Google Drive to use this feature.")){
	     				window.location.href= "/a/oauth/googleAllAuthorization";
	     			}
				}
        	
        }
    }
});