import Ember from 'ember';
import notificationMixin from '../../mixins/notification';

export default Ember.Route.extend(notificationMixin, {
	posts : null,
    hasMoreRecords : true,
    isFetching :false,
    nextPageLink : null,
    groupService: Ember.inject.service('group'),
    postService: Ember.inject.service('post'),
    model(params) {
        return this.store.findRecord('post', params.postId);
    },
    init() {
	    this._super(...arguments);
	  },
    setupController: function(controller, model) {
        this._super(controller, model);
        this.hasMoreRecords = true;
        this.nextPageLink = null;
        this.isFetching =false;
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        this.controller.set('controllerRef', this)
         this.controller.set("noRecords", false);
        this.listenComments();
    },
    willDestroy : function(){
    	this.unlistenComments();
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
    			debugger;
    			var posts = this.controller.get("posts");
    			var index = posts.indexOf(post);
    			if(index > -1){
    				resp1.set('isEditing' , false)
    				posts.replace(index, 1, resp1);
    			}else{
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
        	this.transitionTo('');
        },
    }
});