import Ember from 'ember';
import ajaxMixin from '../../mixins/ajax';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(ajaxMixin, {


    model(params) {
    	const groupAdapter = this.store.adapterFor('group');
        return groupAdapter.findPublicRecord( params.groupId);
    },
    init() {
	    this._super(...arguments);
	  },
    setupController: function(controller, model) {
        this._super(controller, model);
       
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        var bgImageSrc  = "/img/group_bg.jpg";
		 this.controller.set("bgImageSrc","/img/group_bg.jpg");
		Ember.run.later(()=>{$('.institute-public-section').css("background" , "url('" +bgImageSrc +"') no-repeat");} , 100)
    },

 
    actions: {
        addMember(){
        	var searchTerm = this.controller.get("userSearchTerm");
        	if( searchTerm.match( /^.+@.+\..+$/)){
        		 this.controller.get("newMembers").pushObject({email : searchTerm});
        		 this.controller.set("userSearchTerm" , "");
        	}
        },
        joinInstituteClick(){
        	var model = this.controller.get("model");
        	if(this.controller.get("isLoggedIn")){
        		const groupAdapter = this.store.adapterFor('group');
        		groupAdapter.joinGroup(model.id);
        		model.set("isJoinRequested" , true);
        		alert("your request has been sent for approval.")
        	}else{
        		var url = "/a/public/login?redirect=/group/" + model.get("id");
        		window.location.href = url;
        	}
        }
     
    }
});