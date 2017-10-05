import Ember from 'ember';
import moment from 'moment';
import authenticationMixin from '../mixins/authentication';

export default Ember.Route.extend(authenticationMixin , {
	profileService: Ember.inject.service('profile'),
    model(params) {
    	var context = this.contextService.fetchContext((result)=>{
    		var user = this.store.createRecord('user', { 
        		lastName: result.loginUser.lastName,
        		firstName: result.loginUser.firstName, 
        		email : result.loginUser.email,
        		photoUrl : result.loginUser.photoUrl,
        		hasUploadedPhoto :  result.loginUser.hasUploadedPhoto,
        		useGoogleDrive : result.loginUser.useGoogleDrive,
        		refreshTokenAccountEmail : result.loginUser.refreshTokenAccountEmail,
        		sendGroupPostEmail : result.loginUser.sendGroupPostEmail  ,
        		sendGroupPostMentionEmail: result.loginUser.sendGroupPostMentionEmail  ,
        		sendPostCommentedEmail : result.loginUser.sendPostCommentedEmail  ,
        		sendCommentMentiondEmail: result.loginUser.sendCommentMentiondEmail  ,
        		sendCommentOnMentiondPostEmail : result.loginUser.sendCommentOnMentiondPostEmail  ,
        		sendCommentReplyEmail : result.loginUser.sendCommentReplyEmail  ,
        		sendCommentOnCommentEmail : result.loginUser.sendCommentOnCommentEmail  ,
        		});
    		return user;
    	});
    	
    	return context;
    },

    init: function() {},
    setupController: function(controller, model) {
        this._super(controller, model);
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        controller.set("isSearchButtonDisabled", Ember.computed.empty("model.searchTerm"));
    },
    
    actions: {
        saveProfile() {
        	if(!this.get("controller").get("isSaving")){
	            var model = this.get("controller").get("model");
	            this.get("controller").set("isSaving", true);
	            model.save().then(result=>{
	            	this.controller.set("isSaving", false);
	            	if(result.code==0){
	            		this.contextService.setLoginUser(result.item);
	            		this.transitionTo('dashboard');
	            	}
	            });
        	}
        },
        
        removePhoto : function(){
        	this.get("profileService").removePhoto().then((result)=>{
        		this.controller.get('model').set('photoUrl', '/img/users/user_1.jpg');
	    	});
        },
        toggleGoogleDrive : function(){
        	if(!this.get("controller").get("isSaving")){
        		this.get("controller").set("isSaving", true);
	        	var model = this.controller.get('model');
	        	model.toggleProperty('useGoogleDrive');
	        	if(model.get('useGoogleDrive')){
	        		window.location.href= "/a/oauth/driveAuthorization";
	        	}else{
	        		this.get("profileService").deauthorizeGoogleDrive().then(result=>{
	        			this.get("controller").set("isSaving", false);
	        		});
	        	}
        	}
        },
        
        toggleValue : function(attribute){
        	var model = this.controller.get('model');
        	model.toggleProperty(attribute);
        }
    }
});