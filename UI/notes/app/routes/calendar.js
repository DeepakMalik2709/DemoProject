import Ember from 'ember';
import authenticationMixin from '../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{
	calendarService: Ember.inject.service('calendar'),
	 groupService: Ember.inject.service('group'),
	model() {
	   },
	   init() {
		    this._super(...arguments);
		  },
   setupController: function(controller, model) {
        this._super(controller, model);
        this.set('calendar', [model]);
        this.controller.set("showLoadingIcon", true);
        var calendarUrl = "https://calendar.google.com/calendar/embed?ctz=Asia/Kolkata&showTitle=false";
        let request = this.get('groupService').fetchMyGroups();
        request.then((groups) => {
        	  for(var i =0; i<groups.length;i++){
        		  var group = groups[i];
        		  if(group.calendarId){
        			  calendarUrl = calendarUrl + "&src=" + group.calendarId ;
        		  }
        	  }
        	  this.controller.set("calendarUrl" , calendarUrl)
        	  Ember.run.later(()=>{ this.controller.set("showLoadingIcon", false);},5000);
        });
    }
});
