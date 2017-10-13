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
	       
	    },
	   
	    actions: {
	    	  
	    	 saveTask(task){
	     		if(!Ember.get(this, "isSaving") && task.comment){
	     			Ember.set(this, "isSaving", true);
	     			Ember.set(task, "isSaving", true);
	     			Ember.set(task, "showLoading", true);
	 	    		task.save().then((resp1) => {
	 	    			Ember.set(this, "isSaving", false);
	 	    			Ember.set(task, "isSaving", false);
	 	    			Ember.set(task, "showLoading", false);
	 	    			var tasks = this.controller.get("feeds");
	 	    			var index = tasks.indexOf(task);
	 	    			if(index > -1){
	 	    				resp1.set('isEditing' , false)
	 	    				tasks.replace(index, 1, resp1);
	 	    			}else{
	 	    				this.initCreateTask();
	 	    				Ember.run.later(()=>{this.component.resetCommentBox();} , 10)
	 	    				tasks.unshiftObject(resp1);
	 	    			}
	 	    		});
	     		}
	     	},
	     	cancelCreateTask(){
	   		  if (this.controller.get("newTask.comment")) {
	   			   let confirmation = confirm("Cancel task ?");
	   	            if (confirmation) {
	   	            	  	this.initCreatePost();
	   	    	    		this.component.resetCommentBox();
	   	    	    		Ember.set(this, "isSaving", false);
	   	            }
	   		  }else{
	   			  	this.initCreateTask();
	   	    		this.component.resetCommentBox();
	   	    		Ember.set(this, "isSaving", false);
	   		  }
	   	},
	 	deleteTask(task){
	         let confirmation = confirm("Are you sure you want to delete task ?");

	         if (confirmation) {
	         	var posts = this.controller.get("feeds");
	 			var index = posts.indexOf(post);
	 			posts.removeAt(index);
	 			this.get("taskService").deleteTask(post.get("groupId"), task.get("id")).then((result)=>{
	         		if(result.code == 0){
	 	    		}
	 	    	});
	         }
	 	}, 

     
	    }
});
