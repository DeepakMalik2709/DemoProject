import Ember from 'ember';
import scrollMixin from '../../mixins/scroll';
import authenticationMixin from '../../mixins/authentication';

	
export default Ember.Route.extend(scrollMixin,authenticationMixin,{
	useGoogleDrive : false,
		init() {
		    this._super(...arguments);
		   },
		model(params) {
			 return this.store.createRecord('new.schedule',params.id);
	    },
	    afterModel(transition) {
			 var context = this.contextService.fetchContext(result=>{
					if(result && result.code==0){
						 var useGoogleDrive = Ember.get(result , "loginUser.useGoogleDrive")
						 if(false == useGoogleDrive){
							 if(confirm("You must grant AllSchool permission to access Calendar and Google drive to use this feature.")){
				        			window.location.href= "/a/oauth/googleAllAuthorization";
				        		}else{
									 this.transitionTo('home');
								 }
				        	}
					}
				});
			  },
	    setupController: function(controller, model) {
	        this._super(controller, model);
	        controller.set('pageTitle', 'Create Schedule');
	      
	    },
	   
	    actions: {
	        
	    }
});
