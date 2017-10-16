import Ember from 'ember';
import scrollMixin from '../../mixins/scroll';
import authenticationMixin from '../../mixins/authentication';
import {ValidationMixin, validate} from "ember-cli-simple-validation/mixins/validate";

	
export default Ember.Route.extend(scrollMixin,authenticationMixin,ValidationMixin,{
		nameValidation: validate("model.title"),
		attendees:null,
		startDate:new Date(),
		endDate:new Date(),
		groupService: Ember.inject.service('group'),
		useGoogleCalendar : false,
	    init() {
		    this._super(...arguments);
		   },
		model() {
			 return this.store.createRecord('new.event');
	    },
	  
	    setupController: function(controller, model) {
	        this._super(controller, model);
	        this.useGoogleDrive = model.get('loginUser.useGoogleCalendar');
	        controller.set('attendees', []);
	        controller.set('startDate',new Date());
	        controller.set('endDate',new Date());
	        controller.set('pageTitle', 'Create Schedule');
	        let request = this.get('groupService').fetchMyGroups();
	        request.then((response) => {
	        	   controller.set("myGroups" ,response );
	        });        
	        controller.set('buttonLabel', 'Save');
	       
	    },
	   
	    actions: {
	        saveEvent(event) {
	        	 this.set("submitted", true);
	             if(!this.get("valid")) {
	            	 return false;
	             }
	        	event.groups=[];
	        	 Ember.set(this.item, "showLoading", true);
	        	event.start = new Date(this.controller.get('startDate'));
	        	event.end = new Date(this.controller.get('endDate'));
	        	this.controller.get('attendees').forEach(function(item) {
	        		event.groups.push({id:item.id,name:item.name});
	        		});
	    		        	
	        	console.log( event.toJSON());

	        	event.save().then(() => this.transitionTo('calendar'));
	        	 Ember.set(this.item, "showLoading", false);
	        	
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
