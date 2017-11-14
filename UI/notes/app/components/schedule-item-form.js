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
//	startDateValidation:Ember.observer('startDate', function() {
//		return (this.startDate>=this.endDate);
//	 }),
//	
//	endDateValidation:Ember.observer('startDate','endDate', function() {
//		return (this.startDate>=this.endDate);
//	 }),
	
//	isDisabled:true,
	 
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
	        	var adminGroups = response.filterBy("isAdmin", true) ;
	        	if(adminGroups.length){
	        		this.set("myGroups" ,adminGroups );
	        	}else{
	        		alert("You can only post tasks to groups where you are admin");
	        	}
	        });
	  },   
	  
	  validation : function(event){
		  if(!Ember.get(event , "title")){
			  alert("Please enter the title");
			  return false;
		  }
		  if(!Ember.get(event , "location")){
			  alert("Please enter the location");
			  return false;
		  }
		  if(this.startDate>this.endDate){
			  alert("Schedule End time must be greater than start time");
			  return false;
		  }
		  if(!this.attendees.id){
			  alert("Please select at least one group as attendee.");
			  return false;
		  }
		  
		  
		  return true;
	  },
	  
	  
    actions: {
        saveEvent(event) {
        	if(this.validation(event)){
        		this.set("submitted", true);
            	event.groups=[];
            	event.groupId=this.attendees.id;
            	event.start = new Date(this.startDate);
            	event.end = new Date(this.endDate);
            	/*this.attendees.forEach(function(item) {
            		event.groups.push({id:item.id,name:item.name});
            	});*/
        		        	
            	console.log( event.toJSON());

            	//event.save().then(() => this.transitionTo('calendar'));
            	event.save(event).then((resp1) => {
 	    			Ember.set(this, "isSaving", false);
 	    			Ember.set(event, "isSaving", false);
 	    			Ember.set(event, "showLoading", false);
 	    			alert("Schedule posted.")
 	    			this.transitionTo('group.posts', event.groupId);
 	    			
 	    		});
        	}
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