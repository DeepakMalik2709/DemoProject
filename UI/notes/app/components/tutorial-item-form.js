import Ember from 'ember';

export default Ember.Component.extend({
    buttonLabel: 'Save',
    languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
    useGoogleDrive : false,
    contextService: Ember.inject.service('context'),
    postService: Ember.inject.service('post'),
	 init() {
	    this._super(...arguments);
	    this.useGoogleDrive = Ember.get(this.get("contextService").fetchContext().get("loginUser"), "useGoogleDrive");
	  },
    getYoutubeVideoId: function(url) {
    	if(url){
	        var regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
	        var match = url.match(regExp);
	
	        if (match && match[2].length == 11) {
	            return match[2];
	        } else {
	            return '';
	        }
    	}
    	return null;
    },
    actions: {
        addTag(tag) {
            var thisTags = this.item.get("tags");
            var index = thisTags.indexOf(tag);
            if(index < 0 && tag){
            	 thisTags.pushObject(tag);
    		}
        },
        removeTagFromTutorial(tag) {
            var thisTags = this.item.get("tags");
            thisTags.removeObject(tag);
        },

        changeLanguage(lang) {
        	  this.item.set("language", Ember.get( lang , "id"));
        },
    	addLanguage(lang) {
            var thisTags = this.item.get("languagesUI");
            var index = thisTags.indexOf(lang);
            if(index < 0){
            	thisTags.pushObject(lang);
    		}
          //  thisTags =  thisTags.uniqBy('id');
        },
        removeLanguage(lang) {
            var thisTags = this.item.get("languagesUI");
            thisTags.removeObject(lang);
        },
        saveTutorial(param) {
            if (this.item.get("convertToEmbed")) {
                var videoId = this.getYoutubeVideoId(this.item.get("url"));
                if (videoId) {
                    this.item.set("url", "https://www.youtube.com/embed/" + videoId);
                }
            }
           this.sendAction('saveTutorial', param);
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
      }
    }
});