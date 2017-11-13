import Ember from 'ember';

export default Ember.Component.extend({

	validationErrors:[],
	attendees:null,
	startDate:new Date(),
	endDate:new Date(),
	contextService: Ember.inject.service('context'),
	groupService: Ember.inject.service('group'),
	titleValidation: Ember.computed.empty('model.title'),
	locationValidation: Ember.computed.empty('model.location'),
	attendeesValidation:Ember.computed.empty('attendees'),
	startDateValidation:false,/*Ember.observer('model.start', function() {
		console.log("value");
	 }),*/
	endDateValidation:false,/*Ember.observer('endDate', function() {
		return (this.startDate>this.endDate);
	 }),
	*/
	useGoogleCalendar : false,
	 init() {
	    this._super(...arguments);
	    this.useGoogleCalendar = Ember.get(this.get("contextService").fetchContext().get("loginUser"), "useGoogleCalendar");
	    this.set("controllerRef" , this.controllerRef);
	    this.initNewComment();
	  },
	initNewComment(){
		  this.set("attendees" , []);
		  this.set("buttonLabel" ,  'Save');
		
		  let request = this.get('groupService').fetchMyGroups();
	        request.then((response) => {
	        	   this.set("myGroups" ,response );
	        });
	  },   
    actions: {
        saveEvent(event) {
        	 this.set("submitted", true);
            
        	event.groups=[];
        	event.groupId=this.attendees.id;
        	event.start = new Date(this.startDate);
        	event.end = new Date(this.endDate);
        	/*this.attendees.forEach(function(item) {
        		event.groups.push({id:item.id,name:item.name});
        	});*/
    		        	
        	console.log( event.toJSON());

        	event.save().then(() => this.transitionTo('calendar'));
        	 
        	
        },
        cancelClicked(item) {
        	 this.transitionTo('calendar');
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