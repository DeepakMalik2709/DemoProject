import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{


    model(params) {
        return this.store.findRecord('institute', params.groupId);

    },

    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Update Institute');
        controller.set('buttonLabel', 'Update');
    },
    renderTemplate() {
        this.render('institute/upsert');
    },



    actions: {

    	 cancelClicked(group) {
    		 this.transitionTo('institute.view', group.id);
         },

         saveGroup(group) {
        		group.set("isSaving", true);
         	group.save().then((resp) => {
         		group.set("isSaving", false);
         		this.transitionTo('institute.view', group.id);
             });
         },
         error(reason){
         	this.transitionTo('dashboard');
         },
    }
});