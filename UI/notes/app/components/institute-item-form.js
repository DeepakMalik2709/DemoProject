import Ember from 'ember';

export default Ember.Component.extend({
    buttonLabel: 'Save',
    userSearchTerm : '',
    bgImageSrc : null,
    postService: Ember.inject.service('post'),
    init(){
    	this._super(...arguments);
    	if(Ember.get(this.item, "bgImagePath")){
    		console.log(Ember.get(this.item, "bgImagePath"))
    		this.bgImageSrc  = "/a/public/file/preivew?id=" + Ember.get(this.item, "bgImagePath")
    	}
    } ,
    actions: {

    	saveInstitute(param) {
           this.sendAction('saveInstitute', param);
        },

        cancelClicked(param) {
            this.sendAction('cancelClicked', param);
        },
        addMember(){
        	var searchTerm = this.get("userSearchTerm");
        	if( searchTerm.match( /^.+@.+\..+$/)){
        		this.item.get("members").pushObject({email : searchTerm});
        		this.set("userSearchTerm" , "");
        	}
        },
        removeMember(member) {
            var membersList = this.item.get("members");
            membersList.removeObject(member);
        },
        uploadPhoto (evt){
        	if(evt && evt.target && evt.target.files && evt.target.files.length){
			  var files = evt.target.files;
			  
			  Ember.set(this.item, "showLoading", true);
			  Ember.set(this.item, "isSaving", true);
				this.get("postService").uploadFile(files).then((result)=>{
					  Ember.set(this.item, "showLoading", false);
					  Ember.set(this.item, "isSaving", false);
		    		if(result.code == 0  ){
		    			var photo = result.items[0];
		    			Ember.set(this, "item.bgImageFile",photo);
		    			Ember.set(this, "bgImageSrc", "/a/secure/group/file/thumbnail?name=" + photo.serverName);
		    			Ember.set(this.item, "bgImageId",photo.serverName);
		    		}
		    	});
        	}
        },
        removePhoto(){
        	Ember.set(this.item, "bgImageId","");
        	Ember.set(this, "bgImageSrc","");
        	Ember.set(this, "item.bgImageFile",null);
        	
        }
    }
});