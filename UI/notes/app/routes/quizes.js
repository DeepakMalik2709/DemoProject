import Ember from 'ember';
import authenticationMixin from '../mixins/authentication';
export default Ember.Route.extend(authenticationMixin,{

	model() {

	   },
	   init() {
		    this._super(...arguments);
		  },
			setupController: function(controller, model) {
					 this._super(controller, model);
			 }

});
