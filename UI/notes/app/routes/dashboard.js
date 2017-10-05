import Ember from 'ember';
import tutorialMixin from '../mixins/tutorial';
import authenticationMixin from '../mixins/authentication';

const {    inject, computed } = Ember;
export default Ember.Route.extend(authenticationMixin, tutorialMixin,{

    model() {
    	 var dashboard = Ember.Object.create({
             loginUser: null,
             myTutorials: [],
             myTutorialsNextLink: "",
             isLoading: false,
             posts:[]
         });
         return dashboard;
    },
    context: null,
    init: function() {

    },

    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set("isLoggedIn", Ember.computed.notEmpty("model"));
        this.tutorialService.fetchMyTutorialList().then((result)=>{
        	if(result.code == 0){        	
        		 let myTutorials = result.items;
        		
        		 model.set("myTutorials", myTutorials);
        		 model.set("myTutorialsNextLink", result.nextLink);
        		 this.cleanupAllTutorials(myTutorials);
        	}
        });
        this.contextService.fetchContext((result)=>{
        	if(result.code==0){
        		 model.set("loginUser", result.loginUser);
        	}
        });
    },

  
    actions: {

       /* doNavbarSearch() {
            var searchTerm = this.get('controller.searchTerm')
            this.set('controller.searchTerm', '');
            this.transitionTo('application', {
                queryParams: {
                    q: searchTerm
                }
            });
        }*/
    }
});