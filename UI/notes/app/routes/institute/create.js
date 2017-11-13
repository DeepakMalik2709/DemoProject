import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin, {
	queryParams: {
	    name: {
	        refreshModel: true
	      }
	    },
    model(params) {
    	var json = {};
    	if(params.name){
    		json = {name : params.name};
    	}
        return this.store.createRecord('institute' ,json );

    },
   /* instituteService: Ember.inject.service('institute'),*/
    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Create Institute');
        controller.set('buttonLabel', 'Create');
    },
    renderTemplate() {
        this.render('institute/upsert');
    },



    actions: {


        cancelClicked(tutorial) {
            this.transitionTo('home');
        },

        saveInstitute(group) {
        	group.set("isSaving", true);
        	group.save().then((resp) => {
        		group.set("isSaving", false);
        		this.contextService.fetchContext(function(resp){
        			Ember.get(resp, "institutes").pushObject(resp);
        		});
        		
        		this.transitionTo('institute.view', resp.id);
            });
        },
        error(reason){
         	this.transitionTo('dashboard');
         },
    }
});