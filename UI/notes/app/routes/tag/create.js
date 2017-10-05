import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin, {


    model() {
        return this.store.createRecord('tag');

    },
    tagService: Ember.inject.service('tag'),
    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Create Tag');
        controller.set('buttonLabel', 'Create');
    },
    renderTemplate() {
        this.render('tag/upsert');
    },



    actions: {


        cancelClicked(tutorial) {
        	this.transitionTo('tag.view');
        },

        saveTag(tag) {
        	tag.set("isSaving", true);
        	tag.save().then((resp) => {
        		tag.set("isSaving", false);
        		this.transitionTo('tag.view');
            });
        }
    }
});