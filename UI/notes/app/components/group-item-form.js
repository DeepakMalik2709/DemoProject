import Ember from 'ember';

export default Ember.Component.extend({
    buttonLabel: 'Save',
    userSearchTerm : '',
    languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
    sharingOptions : [{ id : "PRIVATE" , label : "Private"},{ id : "PUBLIC" , label : "Public"}],
    getYoutubeVideoId: function(url) {
        var regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
        var match = url.match(regExp);

        if (match && match[2].length == 11) {
            return match[2];
        } else {
            return '';
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
    }
});