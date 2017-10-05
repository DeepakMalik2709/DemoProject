import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{


    model(params) {
        return this.store.findRecord('tag', params.tagId);

    },

    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Update Tag');
        controller.set('buttonLabel', 'Update');
    },
    renderTemplate() {
        this.render('tag/upsert');
    },



    actions: {
    	 cancelClicked(tag) {
    		 this.transitionTo('tag.view');
         },

         saveTag(tag) {
        		tag.set("isSaving", true);
         	tag.save().then((resp) => {
         		tag.set("isSaving", false);
         		this.transitionTo('tag.view');
             });
         },
         error(reason){
         	this.transitionTo('dashboard');
         },
    }
});