import Ember from 'ember';
import authenticationMixin from '../../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{
		groupService: Ember.inject.service('group'),
		contextService: Ember.inject.service('context'),
		postService: Ember.inject.service('post'),
		model() {
			 return this.store.createRecord('post');
	    },
	    afterModel(transition) {
			 var context = this.contextService.fetchContext(result=>{
					if(result && result.code==0){
						 var useGoogleDrive = Ember.get(result , "loginUser.useGoogleDrive")
						 if(false == useGoogleDrive){
				        		if(confirm("AllSchool needs access to Google Drive and Calendar to create Tasks. Would you like to grant permission now ?")){
				        			window.location.href= "/a/oauth/googleAllAuthorization";
				        		}else{
									 this.transitionTo('home');
								 }
				        	}
					}
				});
			  },
	    taskService: Ember.inject.service('task'),
	    useGoogleDrive : false,
	    init() {
		    this._super(...arguments);
		  },
		    renderTemplate() {
		        this.render('group/task/upsert');
		    },
	    setupController: function(controller, model) {
	        this._super(controller, model);
	        this.controller.set("pageTitle", 'Create task');
	        this.controller.set("saveButtonLabel", 'Save');
	        this.controller.set("selectedGroups", []);
	        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
	       	
	        let request = this.get('groupService').fetchMyGroups();
	        request.then((response) => {
	        	var adminGroups = response.filterBy("isAdmin", true) ;
	        	if(adminGroups.length){
	        		this.controller.set("myGroups" ,adminGroups);
	        	}else{
	        		alert("You can only post tasks to groups where you are admin");
	        	}
	        	   
	        });   
	      
	    },
	   
	    actions: {
	    
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
	     	
	     			Ember.set(task, "showLoading", true);
	     			for(var i =0 ;i < selectedGroups.length ;i++){
	     				Ember.get(task,"groupIds").pushObject(selectedGroups[i].id);
	     			}

	     			const adapter = this.store.adapterFor('post');
	     			adapter.saveTask(task).then((resp1) => {
	 	    			Ember.set(this, "isSaving", false);
	 	    			Ember.set(task, "isSaving", false);
	 	    			Ember.set(task, "showLoading", false);
	 	    			alert("Task posted.")
	 	    			this.transitionTo('group.posts',  Ember.get(resp1,"groupId"));
	 	    			
	 	    		});
	     		}
	     	
	    }
});
