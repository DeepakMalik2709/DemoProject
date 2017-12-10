import Ember from 'ember';
import ajaxMixin from '../../mixins/ajax';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(ajaxMixin, {

	instituteAdapter : null,
    model(params) {
        return this.store.findRecord('institute', params.instituteId);
    },
    init() {
	    this._super(...arguments);
	  },
    setupController: function(controller, model) {
        this._super(controller, model);
        this.instituteAdapter = this.store.adapterFor('institute');
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        if(model.get( "bgImagePath")){
    		var bgImageSrc  = "/a/public/file/preivew?id=" + model.get( "bgImagePath")
    		 this.controller.set("bgImageSrc",bgImageSrc);
    		Ember.run.later(()=>{$('.institute-public-section').css("background" , "url('" +bgImageSrc +"') no-repeat");} , 100)
    	}
        this.instituteAdapter.fetchChildGroups(model.id).then((result)=>{
        	if(result.code == 0){
        		if(result.items && result.items.length){
					this.controller.set( "childGroups" , result.items)
				}
        	}
        });
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
        		this.instituteAdapter.joinInstitute(model.id);
        		model.set("isJoinRequested" , true);
        		alert("your request has been sent for approval.")
        	}else{
        		var url = "/a/public/login?redirect=/institute/" + model.get("id");
        		window.location.href = url;
        	}
        },
        
     
    }
});