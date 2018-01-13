import Ember from 'ember';
import instituteMixin from '../mixins/institute';

export default Ember.Component.extend(instituteMixin, {
	init(){
		this._super(...arguments);
	},
	  didInsertElement() {
	    let _this = this;
	    Ember.$(".group-user-list").scroll(function() {
	      if(Ember.$(this).scrollTop() + Ember.$(this).innerHeight() >= Ember.$(this)[0].scrollHeight) {
	        _this.sendAction('fetchMoreMembers'); // Triggering passed controller’s action
	      }
	    });
	  },
	 actions: {
		 onchangeMemberPosition(member, roles, selectBox){
			  Ember.set(member, "roles", roles);
			  Ember.set(member, "isUpdated" ,true);
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
	 }
});