import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{
		
		model() {
			 return this.store.createRecord('new.event');

	    },

	    setupController: function(controller, model) {
	    	   var names = ['kkuldeepjoshi5@gmail.com', 'deepak.m18@fms.edu'];
	        this._super(controller, model);
	        controller.set('pageTitle', 'Create Schedule');
	        controller.set('names',names);
	     
	        controller.set('buttonLabel', 'Save');
	    },
	    actions: {
	    	
	        saveEvent(event) {
	    		// event.attendees=controller.get('attendees');
	        	event.save().then(() => this.transitionTo('calendar'));
	        	
	        },
	        willTransition(transition) {
	         //   let model = this.controller.get('model');
	            /*
	             * if(model.get('hasDirtyAttributes')){ let confirmation =
	             * confirm("leave without saving ? "); if(confirmation){
	             * model.rollbackAttributes(); }else{ transition.abort(); } }
	             */
	        }
	    }
});
