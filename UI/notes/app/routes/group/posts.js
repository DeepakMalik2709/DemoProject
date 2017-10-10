import Ember from 'ember';
import scrollMixin from '../../mixins/scroll';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(scrollMixin,authenticationMixin,{
	posts : null,
    model(params) {
        return this.store.findRecord('group', params.groupId);
    },
    hasMoreRecords : true,
    nextPageLink : null,
    isFetching :false,
    groupService: Ember.inject.service('group'),
    postService: Ember.inject.service('post'),
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
        this.hasMoreRecords = true;
        this.nextPageLink = null;
        this.isFetching =false;
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        this.controller.set("feeds", []);
        this.controller.set('controllerRef', this)
        this.controller.set("noRecords", false);
        this.initCreatePost();
        this.fetchGroupPosts();
        this.bindScrolling();
    },
    willDestroy : function(){
    	this.unbindScrolling();
    },
    initCreateTab : function(){
    	this.controller.set("showCreatePost", false);
    	this.controller.set("showCreateTask", false);
    },
    initCreatePost : function(){
    	 this.initCreateTab();
    	this.controller.set("showCreatePost", true);
    	var group = this.controller.get("model");
    	 const newPost = this.store.createRecord('post', {
    		 groupId: group.id,
	      }); 
    	  this.controller.set("newPost", newPost);
    },
    initCreateTask : function(){
    	 this.initCreateTab();
    	this.controller.set("showCreateTask", true);
    	var group = this.controller.get("model");
    	 const newTask = this.store.createRecord('task', {
    		 groupId: group.id,
	      }); 
    	  this.controller.set("newTask", newTask);
    },
    fetchGroupPosts : function(){
    	if(this.hasMoreRecords && ! this.isFetching){
    		this.isFetching = true;
	    	var group = this.controller.get("model");
	    	this.get("groupService").fetchGroupPosts(group, this.nextPageLink).then((result)=>{
	    		this.isFetching = false;
	    		if(result.code == 0 ){
	    			var newFeeds = [];
		    		if( result.items && result.items.length){
		    			var thisPosts = result.items;
		    			 newFeeds.pushObjects(thisPosts);
		    		}
		    		if( result.tasks && result.tasks.length){
		    			var thisPosts = result.tasks;
		    			newFeeds.pushObjects(thisPosts);
		    		}
		    		if(newFeeds.length == 0){
	    				this.hasMoreRecords = false;
	    				if( this.controller.get("feeds").length == 0){
	    					this.controller.set("noRecords", true);
	    				}
		    		}else{
		    			 this.controller.get("feeds").pushObjects(newFeeds.sortBy("updatedTime").reverse());
		    			// this.controller.get("feeds");
		    		}
	   			 	this.nextPageLink = result.nextLink;
		    	}
	    	});
	    }
    },
    scrolled: function() {
    	this.fetchGroupPosts();
      },
    actions: {
    	savePost(post){
    		if(!Ember.get(this, "isSaving") && post.comment){
    			Ember.set(this, "isSaving", true);
    			Ember.set(post, "isSaving", true);
    			Ember.set(post, "showLoading", true);
	    		post.save().then((resp1) => {
	    			Ember.set(this, "isSaving", false);
	    			Ember.set(post, "isSaving", false);
	    			Ember.set(post, "showLoading", false);
	    			var posts = this.controller.get("feeds");
	    			var index = posts.indexOf(post);
	    			if(index > -1){
	    				resp1.set('isEditing' , false)
	    				posts.replace(index, 1, resp1);
	    			}else{
	    				this.initCreatePost();
	    				Ember.run.later(()=>{this.component.resetCommentBox();} , 10)
	    				posts.unshiftObject(resp1);
	    			}
	    		});
    		}
    	},
    	deletePost(post){
            let confirmation = confirm("Are you sure you want to delete post ?");

            if (confirmation) {
            	var posts = this.controller.get("feeds");
    			var index = posts.indexOf(post);
    			posts.removeAt(index);
    			this.get("postService").deletePost(post.get("groupId"), post.get("id")).then((result)=>{
            		if(result.code == 0){
    	    		}
    	    	});
            }
    	},
    	cancelCreatePost(){
    		  if (this.controller.get("newPost.comment")) {
    			   let confirmation = confirm("Cancel post ?");
    	            if (confirmation) {
    	            	  	this.initCreatePost();
    	    	    		this.component.resetCommentBox();
    	    	    		Ember.set(this, "isSaving", false);
    	            }
    		  }else{
    			  	this.initCreatePost();
    	    		this.component.resetCommentBox();
    	    		Ember.set(this, "isSaving", false);
    		  }
    	},
    	cancelEditPost(){
    		this.initCreatePost();
    	},
        error(reason){
        	this.transitionTo('dashboard');
        },
        showCreatePostAction(){
        	this.initCreatePost();
        },
        showCreateTaskAction(){
        	if(this.useGoogleDrive){
        		this.initCreateTask();
        	}else{
        		if(confirm("AllSchool needs access to Google Drive and Calendar to create Tasks. Would you like to grant permission now ?")){
        			window.location.href= "/a/oauth/googleAllAuthorization";
        		}
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