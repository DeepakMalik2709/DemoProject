import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{


    model(params) {
        return this.store.findRecord('group', params.groupId);

    },

    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Update Group');
        controller.set('buttonLabel', 'Update');
    },
    renderTemplate() {
        this.render('group/upsert');
    },



    actions: {

    	 cancelClicked(group) {
    		 this.transitionTo('group.view', group.id);
         },

         saveGroup(group) {
        		group.set("isSaving", true);
         	group.save().then((resp) => {
         		group.set("isSaving", false);
         		this.transitionTo('group.view', group.id);
                /* if (resp.code === 0) {
                     this.transitionTo('group.view', resp.item.id);
                 } else if (resp.message) {
                     alert(resp.message);
                 }*/
             });
         },
         error(reason){
         	this.transitionTo('dashboard');
         },
    }
});