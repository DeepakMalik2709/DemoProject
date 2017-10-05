import Ember from 'ember';
import ajaxMixin from '../../mixins/ajax';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(ajaxMixin,authenticationMixin, {


    model() {
    },
    tagService: Ember.inject.service('tag'),
    init() {
	    this._super(...arguments);
	  },
	  
    setupController: function(controller, model) {
        this._super(controller, model);
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
    },

  


    actions: {
        editTag(tag) {
        	this.transitionTo('tag.edit', tag.id);
        },
        createTag() {
        	this.transitionTo('tag.create');
        },
       
    }
});