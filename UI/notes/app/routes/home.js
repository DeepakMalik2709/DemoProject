import Ember from 'ember';
import scrollMixin from '../mixins/scroll';
import notificationMixin from '../mixins/notification';
import authenticationMixin from '../mixins/authentication';

export default Ember.Route.extend(scrollMixin,authenticationMixin ,notificationMixin , {
	posts : null,
    hasMoreRecords : true,
    isFetching :false,
    nextPageLink : null,
    groupService: Ember.inject.service('group'),
    postService: Ember.inject.service('post'),
    taskService: Ember.inject.service('task'),
    init() {
	    this._super(...arguments);
	  },
    setupController: function(controller, model) {
        this._super(controller, model);
        this.set('posts', []);
        this.hasMoreRecords = true;
        this.nextPageLink = null;
        this.isFetching =false;
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        this.controller.set("posts", this.posts);
        this.controller.set('controllerRef', this)
         this.controller.set("noRecords", false);
        this.fetchMyHomePosts();
        this.bindScrolling();
        this.listenComments();
    },
    willDestroy : function(){
    	this.unbindScrolling();
    	this.unlistenComments();
    },
    initCreatePost : function(){
    	var group = this.controller.get("model");
    	 const newPost = this.store.createRecord('post', {
    		 groupId: group.id,
	      }); 
    	  this.controller.set("newPost", newPost);
    },
    fetchMyHomePosts : function(){
    	if(this.hasMoreRecords && ! this.isFetching){
    		this.isFetching = true;
	    	var group = this.controller.get("model");
	    	this.get("groupService").fetchMyHomePosts( this.nextPageLink).then((result)=>{
	    		this.isFetching = false;
	    		if(result.code == 0 ){
	    			if(result.items && result.items.length){
	    			var thisPosts = result.items;
	    			 this.controller.get("posts").pushObjects(thisPosts);
	    			}else{
	    				this.hasMoreRecords = false;
	    				if( this.controller.get("posts").length == 0){
	    					this.controller.set("noRecords", true);
	    				}
	    			}
	    			 this.nextPageLink = result.nextLink;
	    			
	    		}
	    	});
	    }
    },
    scrolled: function() {
    	this.fetchMyHomePosts();
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
    			var posts = this.controller.get("posts");
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
            	var posts = this.controller.get("posts");
    			var index = posts.indexOf(post);
    			posts.removeAt(index);
    			this.get("postService").deletePost(post.get("groupId"), post.get("id")).then((result)=>{
            		if(result.code == 0){
    	    		}
    	    	});
            }
    	},
    	
    	cancelEditPost(){
    		this.initCreatePost();
    	},
        error(reason){
        	this.transitionTo('dashboard');
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
        editTask(task){
       	 this.transitionTo('group.task.edit', task.id);
    	},
    }
});