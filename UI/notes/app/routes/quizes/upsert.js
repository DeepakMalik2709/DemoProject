import Ember from 'ember';

export default Ember.Route.extend({
	
    contextService: Ember.inject.service('context'),
	model() {
        return this.store.createRecord('group');
    },
	setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Create Quiz');
        controller.set('saveButtonLabel', 'Create Quiz');
        var context = this.contextService.fetchContext((result)=>{
        	 controller.set('institutes', result.institutes);
        });
    },
});
