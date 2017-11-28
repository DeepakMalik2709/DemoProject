import Ember from 'ember';

export default Ember.Component.extend({

	validationErrors:[],
	attendees:null,
	contextService: Ember.inject.service('context'),
	groupService: Ember.inject.service('group'),
	titleValidation: Ember.computed.empty('model.title'),
	locationValidation: Ember.computed.empty('model.location'),
	attendeesValidation:Ember.computed.empty('attendees'),	 
	useGoogleCalendar : false,
	router: Ember.inject.service("-routing"),
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
		  if(event.start>event.end){
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
        	console.log( event.toJSON());
        	if(this.validation(event)){
        		this.set("submitted", true);
            	event.groups=[];
            	event.groupId=this.attendees.id;
            	event.save(event).then((resp1) => {
 	    			
 	    			alert("Schedule posted.");
 	    			this.get("router").transitionTo('group.posts', event.groupId);
 	    		//	this.transitionTo('group.posts', event.groupId);
 	    			
 	    		});
        	}
        },
        willTransition(transition) {
        
        }
    }
  
});