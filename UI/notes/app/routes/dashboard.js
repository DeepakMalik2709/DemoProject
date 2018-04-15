import Ember from 'ember';
import tutorialMixin from '../mixins/tutorial';
import utilsMixin from '../mixins/utils';
import authenticationMixin from '../mixins/authentication';

const {    inject, computed } = Ember;
export default Ember.Route.extend(authenticationMixin, tutorialMixin, utilsMixin,{
	dashboardService: Ember.inject.service('dashboard'),
	model() {
    	 var dashboard = Ember.Object.create({
             loginUser: null,
             myTutorials: [],
             myTutorialsNextLink: "",
             todaySchedules: [],
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
//        this.tutorialService.fetchMyTutorialList().then((result)=>{
//        	if(result.code == 0){
//        		 let myTutorials = result.items;
//
//        		 model.set("myTutorials", myTutorials);
//        		 model.set("myTutorialsNextLink", result.nextLink);
//        		 this.cleanupAllTutorials(myTutorials);
//        	}
//        });
        this.contextService.fetchContext((result)=>{
        	if(result.code==0){
        		 model.set("loginUser", result.loginUser);
        	}
        });

      /*  this.get('dashboardService').dashboardData().then((result) => {
        	var modifiedSchedulesList = [];
        	if(result.code==0){
        		for(var i =0; i<result.todaySchedules.length;i++){
            		var actualSchedule = result.todaySchedules[i];
            		var modifiedSchedule = {};
            		modifiedSchedule.title = actualSchedule.title;
            		modifiedSchedule.eventType = actualSchedule.eventType;

            		var timing = this.dateTimeFormatAMPM(new Date(actualSchedule.start));
            		if(actualSchedule.end != null){
            			timing = timing+" - "+this.dateTimeFormatAMPM(new Date(actualSchedule.end));
            		}
            		modifiedSchedule.timing = timing;

            		modifiedSchedulesList[i] =modifiedSchedule;
            	}
        		model.set("todaySchedules", modifiedSchedulesList);
        	}
        });  */
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
