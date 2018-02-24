import Ember from 'ember';
import scrollMixin from '../../mixins/scroll';
import notificationMixin from '../../mixins/notification';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(scrollMixin,authenticationMixin,notificationMixin,{
	posts : null,
    model(params) {
        return this.store.findRecord('group', params.groupId);
    },
    hasMoreRecords : true,
    nextPageLink : null,
    isFetching :false,
    groupService: Ember.inject.service('group'),
    postService: Ember.inject.service('post'),
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
        this.hasMoreRecords = true;
        this.nextPageLink = null;
        this.isFetching =false;
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        this.controller.set("posts", []);
        this.controller.set('controllerRef', this)
        this.controller.set("noRecords", false);
        this.initCreatePost();
        this.fetchGroupPosts();
        this.bindScrolling();
        hideSidebarMobile();
        this.listenComments();
    },
    willDestroy : function(){
    	this.unbindScrolling();
    	this.unlistenComments();
    	
    },
    initCreateTab : function(){
    	this.controller.set("showCreatePost", false);
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
		    		if(newFeeds.length == 0){
	    				this.hasMoreRecords = false;
	    				if( this.controller.get("posts").length == 0){
	    					this.controller.set("noRecords", true);
	    				}
		    		}else{
		    			 this.controller.get("posts").pushObjects(newFeeds.sortBy("updatedTime").reverse());
		    			// this.controller.get("posts");
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
    			this.controller.set("noRecords", false);
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
    		console.log(reason)
        	this.transitionTo('home');
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
        editTask(task){
        	 this.transitionTo('group.task.edit', task.id);
    	},
	    copyPublicLink(){
	    	let model = this.controller.get('model');
	    	var context = this.contextService.fetchContext((result)=>{
	    		copyToClipboard(result.url + "/group/" + model.id)
	    		alert("Public link copied to clipboard");
	    	});
	    },
    }
});