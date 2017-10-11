import Ember from 'ember';

export default Ember.Route.extend( {
	posts : null,
    hasMoreRecords : true,
    isFetching :false,
    nextPageLink : null,
    groupService: Ember.inject.service('group'),
    taskService: Ember.inject.service('task'),
    model(params) {
        return this.store.findRecord('task', params.taskId);
    },
    init() {
	    this._super(...arguments);
	  },
    setupController: function(controller, model) {
        this._super(controller, model);
        this.set('posts', [model]);
        this.hasMoreRecords = true;
        this.nextPageLink = null;
        this.isFetching =false;
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        this.controller.set("posts", this.posts);
        this.controller.set('controllerRef', this)
         this.controller.set("noRecords", false);
    },
    willDestroy : function(){
    },
    actions: {
    	deleteTask(task){
            let confirmation = confirm("Are you sure you want to delete task ?");

            if (confirmation) {
            	var posts = this.controller.get("posts");
    			var index = posts.indexOf(post);
    			posts.removeAt(index);
    			this.get("taskService").deleteTask(post.get("groupId"), task.get("id")).then((result)=>{
            		if(result.code == 0){
    	    		}
    	    	});
            }
    	}, 
    	
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
    	
    	cancelEditPost(){
    		this.initCreatePost();
    	},
        error(reason){
        	this.transitionTo('');
        },
    }
});