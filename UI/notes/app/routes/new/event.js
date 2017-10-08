import Ember from 'ember';
import scrollMixin from '../../mixins/scroll';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(scrollMixin,authenticationMixin,{
		attendees:null,
		
		groupService: Ember.inject.service('group'),
	    init() {
		    this._super(...arguments);
		  },
		model() {
			 return this.store.createRecord('new.event');
	    },
	  
	    setupController: function(controller, model) {
	        this._super(controller, model);
	       
	        controller.set('attendees', []);
	       
	        controller.set('pageTitle', 'Create Schedule');
	        let request = this.get('groupService').fetchMyGroups();
	        request.then((response) => {
	        	   controller.set("myGroups" ,response );
	        });        
	        controller.set('buttonLabel', 'Save');
	       
	    },
	   
	    actions: {
	    	 
	        saveEvent(event) {
	        	event.groups=[];	        	
	        	this.controller.get('attendees').forEach(function(item) {
	        		event.groups.push({id:item.id,name:item.name});
	        		});
	    		        	
	        	console.log( event.toJSON());
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
