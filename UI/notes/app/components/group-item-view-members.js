import Ember from 'ember';
import instituteMixin from '../mixins/institute';

export default Ember.Component.extend(instituteMixin, {
	init(){
		this._super(...arguments);
		
	},
	  didInsertElement() {

	  },
	 actions: {
		 onchangeMemberPosition(member, roles, selectBox){
			  Ember.set(member, "roles", roles);
			  Ember.set(member, "isUpdated" ,true);
	    	},
	    	fetchMoreMembers(){
	    		 this.sendAction('fetchMoreMembers');
	    	},
	    	updateMember(user){
	    		 this.sendAction('updateMember', user);
	    	},
	    	toggleBlockMember(user){
	    		 this.sendAction('toggleBlockMember', user);
	    	},
	    	deleteMember(user){
	    		 this.sendAction('deleteMember', user);
	    	},
	        toggleUserList(){
	        	 this.model.toggleProperty("showGroupMembers");
	        	return false;
	        },
	        deleteGroupMember(user){
	    		 this.sendAction('deleteGroupMember', user);
	    	},
	 }
});