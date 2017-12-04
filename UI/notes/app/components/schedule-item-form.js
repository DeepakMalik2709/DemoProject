import Ember from 'ember';
import authenticationMixin from '../mixins/authentication';

export default Ember.Component.extend(authenticationMixin,{

	validationErrors:[],
	attendees:null,
	contextService: Ember.inject.service('context'),
	groupService: Ember.inject.service('group'),
	titleValidation: Ember.computed.empty('model.title'),
	locationValidation: Ember.computed.empty('model.location'),
	attendeesValidation:Ember.computed.empty('attendees'),	 
	useGoogleCalendar : false,
	selectedDay:[],
	router: Ember.inject.service('-routing'), 
	days:[],
	 init() {
	    this._super(...arguments);
	    this.useGoogleCalendar = Ember.get(this.get("contextService").fetchContext().get("loginUser"), "useGoogleCalendar");
	    this.set("controllerRef" , this.controllerRef);	   
	    this.days=[ {id:0,name:'Sun',value:'SUNDAY',isActive:false},{id:1,name:'Mon',value:'MONDAY',isActive:false},{id:2,name:'Tues',value:'TUESDAY',isActive:false},
	                {id:3,name:'Wed',value:'WEDNESDAY',isActive:false},{id:4,name:'Thurs',value:'THURSDAY',isActive:false},{id:5,name:'Fri',value:'FRIDAY',isActive:false},
	              {id:6,name:'Sat',value:'SATURDAY',isActive:false}],
	
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
		  if(this.selectedDay==[]){
			  alert("Please select schedule days");
			  return false;
		  }
		  if(!this.attendees.id){
			  alert("Please select at least one group as attendee.");
			  return false;
		  }
		  
		  
		  return true;
	  },
	  
	  
    actions: {
    	
		addDay(day){
			if(this.selectedDay.isAny('value', day.value)){
				var index=this.selectedDay.indexOf(day)
				Ember.set(day,'isActive',false);
				this.selectedDay.splice(index,1);
			}else{
				Ember.set(day,'isActive',true);
				this.selectedDay.push(day);
			}
		},
		addAllDays(){
			if(this.selectedDay.length>6){
				this.selectedDay=[];
				this.get('days').forEach(function(item, index) {		
						Ember.set(item,'isActive',false);
				});	
			}else{
				var objSelectDay = this.selectedDay;
				this.get('days').forEach(function(item, index) {				
					if( !objSelectDay.isAny('value', item.value)){
						Ember.set(item,'isActive',true);
						objSelectDay.push(item);
					}					  
				});				
			}
		},
        saveEvent(event) {
        	if(this.validation(event)){
        		this.set("submitted", true);
            	event.groups=[];
            	event.groupId=this.attendees.id;
            	Ember.set(event, "showLoading", true);
            	event.weekdays=[];
            	this.selectedDay.forEach(function(item, index) {
            		if(!event.weekdays.isAny('value', item.value)){
						event.weekdays.push(item.value);
					}					 
				});
            	 console.log(event.toJSON());
            	event.save(event).then((resp1) => {
            		Ember.set(this, "isSaving", false);
 	    			Ember.set(event, "isSaving", false);
 	    			Ember.set(event, "showLoading", false);
 	    			alert("Schedule posted.");
 	    			this.get('router').transitionTo('group.posts',[event.groupId]); 	    			
 	    		});
        	}
        }
    }
  
});