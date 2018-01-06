import Ember from 'ember';
import authenticationMixin from '../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{
	calendarService: Ember.inject.service('calendar'),
	 groupService: Ember.inject.service('group'),
	model() {
		 return this.store.findRecord('calendar',"1");		 
	   },
	   init() {
		    this._super(...arguments);
		  },
   setupController: function(controller, model) {
        this._super(controller, model);
        this.set('calendar', [model]);
        var calendarUrl = "https://calendar.google.com/calendar/embed?ctz=Asia/Kolkata&showTitle=false";
        let request = this.get('groupService').fetchMyGroups();
        request.then((groups) => {
        	  for(var i =0; i<groups.length;i++){
        		  var group = groups[i];
        		  if(group.calendarId){
        			  console.log(group.calendarId);
        			  calendarUrl = calendarUrl + "&src=" + group.calendarId ;
        		  }
        	  }
        	  this.controller.set("calendarUrl" , calendarUrl)
        });
    }
});
