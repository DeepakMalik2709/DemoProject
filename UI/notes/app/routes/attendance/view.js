import Ember from 'ember';
	
export default Ember.Route.extend({
	
		groupId:'',
		tableData:[],
		init() {
		    this._super(...arguments);
		    this.set("controllerRef" , this.controllerRef);	 
		   
		   },
		model(params) {
			   this.groupId = params.groupId;
			   return this.store.createRecord('attendance');			   
	    },	  
	    setupController: function(controller, model) {
	        this._super(controller, model);
	        model.set("groupId",this.groupId);
	        controller.set('pageTitle', 'Create Schedule');	      
	    },	
	    
	    actions: {
	    	
	    }
});
