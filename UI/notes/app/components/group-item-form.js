import Ember from 'ember';

export default Ember.Component.extend({
    buttonLabel: 'Save',
    userSearchTerm : '',
    languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
    sharingOptions : [{ id : "PRIVATE" , label : "Private"},{ id : "PUBLIC" , label : "Public"}],
    institutes : [],
    singleInstitute  : false,
    hasInstitutes : false,
    firstInstitute : null,
    init() {
	    this._super(...arguments);
	    if(!this.item.id){
	    	if(this.institutesList && this.institutesList.length){
	    		this.hasInstitutes = true;
	    		for(var i=0; i<this.institutesList.length ; i++){
	    			var institute = this.institutesList[i];
	    			this.institutes.pushObject({id :institute.id , label : institute.name });
	    		}
	    		this.singleInstitute = (this.institutes.length==1);
	    		this.firstInstitute = this.institutes[0];
	    	}
	    }
	  },
	  
    actions: {

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

        setSharing(sharing) {
        	  this.item.set("sharing", Ember.get( sharing , "id"));
        },
        saveGroup(param) {
            if (this.item.get("convertToEmbed")) {
                var videoId = this.getYoutubeVideoId(this.item.get("url"));
                if (videoId) {
                    this.item.set("url", "https://www.youtube.com/embed/" + videoId);
                }
            }
           this.sendAction('saveGroup', param);
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
        addTag(tag) {
            var thisTags = this.item.get("tags");
            var index = thisTags.indexOf(tag);
            if(index < 0 && tag){
            	 thisTags.pushObject(tag);
    		}
        },
        removeTagFromGroup(tag) {
            var thisTags = this.item.get("tags");
            thisTags.removeObject(tag);
        },
        setInstitute(institute){
        	Ember.set(this.item, "instituteId" ,institute.id )
        }
    }
});