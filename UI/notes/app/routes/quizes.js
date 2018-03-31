import Ember from 'ember';
import authenticationMixin from '../mixins/authentication';

export default Ember.Route.extend(authenticationMixin,{

	model() {
		  return this.store.findAll('quiz');
	   }

});
