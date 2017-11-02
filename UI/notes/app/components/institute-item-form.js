import Ember from 'ember';

export default Ember.Component.extend({
    buttonLabel: 'Save',
    userSearchTerm : '',
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
    }
});