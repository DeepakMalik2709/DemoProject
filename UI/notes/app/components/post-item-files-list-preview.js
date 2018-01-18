import Ember from 'ember';

export default Ember.Component.extend({
	contextService: Ember.inject.service('context'),
	postService: Ember.inject.service('post'),
	useGoogleDrive : false,
	 init() {
		    this._super(...arguments);
		    this.useGoogleDrive = Ember.get(this.get("contextService").fetchContext().get("loginUser"), "useGoogleDrive");
	 },
	
	 
	 actions: {
	       removeFile(itemToRemove) {
	    	   Ember.get(this.item, "files").removeObject(itemToRemove);
       },
	 }
});