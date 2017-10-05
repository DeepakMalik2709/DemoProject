import Ember from 'ember';
import authenticationMixin from '../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{
	calendarService: Ember.inject.service('calendar'),
	model() {
		 return this.store.findRecord('calendar',"1");		 
	   },
	   init() {
		    this._super(...arguments);
		  },
   setupController: function(controller, model) {
        this._super(controller, model);
        this.set('calendar', [model]);
    }
});
