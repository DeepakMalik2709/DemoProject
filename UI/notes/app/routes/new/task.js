import Ember from 'ember';
import scrollMixin from '../../mixins/scroll';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(scrollMixin,authenticationMixin,{
		groupService: Ember.inject.service('group'),
		contextService: Ember.inject.service('context'),
		postService: Ember.inject.service('post'),
		model() {
			 return this.store.createRecord('task');
	    },
	    taskService: Ember.inject.service('task'),
	    useGoogleDrive : false,
	    init() {
		    this._super(...arguments);
		    this.contextService.fetchContext(result=>{
				if(result && result.code==0){
					 this.useGoogleDrive = Ember.get(result , "loginUser.useGoogleDrive")
				}
			});
		  },
	    setupController: function(controller, model) {
	        this._super(controller, model);
	        this.controller.set("selectedGroups", []);
	        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
	       	if(false == this.useGoogleDrive){
        		if(confirm("AllSchool needs access to Google Drive and Calendar to create Tasks. Would you like to grant permission now ?")){
        			window.location.href= "/a/oauth/googleAllAuthorization";
        		}
        	}
	        let request = this.get('groupService').fetchMyGroups();
	        request.then((response) => {
	        	   this.controller.set("myGroups" ,response );
	        });   
	       var hoursArray = [];
	       for(var i = 0 ; i < 24; i++){
	    	   hoursArray.push({label : (i) , id :i});
	       }
	       var minutesArray = [];
	       for(var i = 0 ; i < 50; i = i+15){
	    	   minutesArray.push({label : i , id :i});
	       }
	       this.controller.set("minutesArray", minutesArray);
	       this.controller.set("hoursArray",hoursArray);
	    },
	   
	    actions: {
	    	addHour(hour) {
	    		 this.controller.set("selectedHour", hour);
	        },
	        addMinutes(minutes) {
	    		 this.controller.set("selectedMinutes", minutes);
	        },
	    	 saveTask(task){

	    		 if(!Ember.get(task , "title")){
	    			 alert("please give your task a title.")
	    		 }
	    				 
	    		 if(!Ember.get(task , "comment") && !Ember.get(task, "files").length){
	    			 alert("Please upload or type task description.")
	    			 return;
	    		 }
	    		var selectedGroups =  this.controller.get("selectedGroups");
	    		 if(! selectedGroups.length){
	    			 alert("Please select group to post task.")
	    			 return;
	    		 }
	     		if(!Ember.get(this, "isSaving") ){
	     			Ember.set(this, "isSaving", true);
	     			Ember.set(task, "isSaving", true);
	     			Ember.set(task, "showLoading", true);
	     			for(var i =0 ;i < selectedGroups.length ;i++){
	     				Ember.get(task,"groupIds").pushObject(selectedGroups[i].id);
	     			}
		        	var date = Ember.get(task,"deadlineTime");
		        	if(date){
		        		date = date.getTime();
		        		var selectedHour = this.controller.get("selectedHour.id");
		     			var selectedMinutes = this.controller.get("selectedMinutes.id");
		     			if(selectedHour){
		     				date += (selectedHour * 60 * 60000);
		     			}
		     			if(selectedMinutes){
		     				date += selectedMinutes* 60000;
		     			}
		     			Ember.set(task, 'deadlineTime', date)
		        	}
	     			
	 	    		task.save().then((resp1) => {
	 	    			Ember.set(this, "isSaving", false);
	 	    			Ember.set(task, "isSaving", false);
	 	    			Ember.set(task, "showLoading", false);
	 	    			alert("Task posted.")
	 	    			console.log(resp1)
	 	    			this.transitionTo('group.posts',  Ember.get(resp1,"groupId"));
	 	    			
	 	    		});
	     		}
	     	}
	    }
});
