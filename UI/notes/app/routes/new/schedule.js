import Ember from 'ember';
import scrollMixin from '../../mixins/scroll';
import authenticationMixin from '../../mixins/authentication';

	
export default Ember.Route.extend(scrollMixin,authenticationMixin,{
		
		init() {
		    this._super(...arguments);
		   },
		model(params) {
			 return this.store.createRecord('new.schedule',params.id);
	    },
	  
	    setupController: function(controller, model) {
	        this._super(controller, model);
	        controller.set('pageTitle', 'Create Schedule');
	      
	    },
	   
	    actions: {
	        
	    }
});
