import Ember from 'ember';
import utilsMixin from '../mixins/utils';

export default Ember.Controller.extend(utilsMixin, {
	
	profileService: Ember.inject.service('profile'),
	
	  actions: {

		  uploadPhoto (evt){
	        	if(evt && evt.target && evt.target.files){
				  var file = evt.target.files[0];
				  if(file && file.size < 1048576) {
					  var reader = new FileReader();
					  reader.readAsArrayBuffer(file);
						reader.onloadend = (e) => {
							var base64Data = btoa(this.readAsBinary(reader.result));
							var srcString = "data:" + file.type + ";base64," + base64Data;
							this.get('model').set('photoUrl', srcString);
						}
						this.set("isSaving", true);
						this.get("profileService").uploadPhoto(file).then((result)=>{
							this.set("isSaving", false);
				    		if(result.code == 0  ){
				    			this.contextService.updateProfilePhoto();
				    		}
				    	});
						
		        	}else{
		        		alert('Image size should be less than 1 MB.');
		        	}
	        	}
	        }
	  },
});
