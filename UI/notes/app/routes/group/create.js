import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin, {


    model() {
        return this.store.createRecord('group');

    },
    groupService: Ember.inject.service('group'),
    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Create Group');
        controller.set('buttonLabel', 'Create');
    },
    renderTemplate() {
        this.render('group/upsert');
    },



    actions: {


        cancelClicked(tutorial) {
            this.transitionTo('index');
        },

        saveGroup(group) {
        	group.set("isSaving", true);
        	group.save().then((resp) => {
        		this.get("groupService.myGroups").pushObject(resp.item); 
        		group.set("isSaving", false);
        		this.transitionTo('group.view', resp.id);
            });
        },
        willTransition(transition) {
            let model = this.controller.get('model');
            /*
             * if(model.get('hasDirtyAttributes')){ let confirmation =
             * confirm("leave without saving ? "); if(confirmation){
             * model.rollbackAttributes(); }else{ transition.abort(); } }
             */
        }
    }
});