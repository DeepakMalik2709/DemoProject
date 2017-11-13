import Ember from 'ember';
import authenticationMixin from '../../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{
		groupService: Ember.inject.service('group'),
		contextService: Ember.inject.service('context'),
		postService: Ember.inject.service('post'),
		model(params) {
			 return this.store.findRecord('post', params.taskId);
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
		    renderTemplate() {
		        this.render('group/task/upsert');
		    },
	    setupController: function(controller, model) {
	        this._super(controller, model);
	        this.controller.set("pageTitle", 'Edit task : ' + model.get("groupName") + " - " + model.get("title"));
	        this.controller.set("saveButtonLabel", 'Update');
	        this.controller.set("selectedGroups", []);
	        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
	       	if(false == this.useGoogleDrive){
        		if(confirm("AllSchool needs access to Google Drive and Calendar to create Tasks. Would you like to grant permission now ?")){
        			window.location.href= "/a/oauth/googleAllAuthorization";
        		}
        	}
/*	        let request = this.get('groupService').fetchMyGroups();
	        request.then((response) => {
	        	   this.controller.set("myGroups" ,response );
	        	   var selectedGroup = response.filterBy("id", model.get( "groupId"))[0];
	        	   if(selectedGroup){
	        		   this.controller.get("selectedGroups").pushObject(selectedGroup);
	        	   }
	        });*/   
	        Ember.run.later(()=>{   $(".post-description").html(model.get("comment"));} , 100)
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
	 /*   		 if(! selectedGroups.length){
	    			 alert("Please select group to post task.")
	    			 return;
	    		 }*/
	     		if(!Ember.get(this, "isSaving") ){
	     			Ember.set(this, "isSaving", true);
	     			Ember.set(task, "isSaving", true);
	     			Ember.set(task, "showLoading", true);
	     			var groupIds = [];
	     			/*for(var i =0 ;i < selectedGroups.length ;i++){
	     				groupIds.pushObject(selectedGroups[i].id);
	     			}
	     			Ember.set(task,"groupIds", groupIds)*/
	     			
	     			const adapter = this.store.adapterFor('post');
	     			adapter.saveTask(task).then((resp1) => {
	 	    			Ember.set(this, "isSaving", false);
	 	    			Ember.set(task, "isSaving", false);
	 	    			Ember.set(task, "showLoading", false);
	 	    			alert("Task saved.")
	 	    			this.transitionTo('group.posts',  Ember.get(resp1,"groupId"));
	 	    		});
	     		}
	     	}
	    }
});
