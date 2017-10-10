import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Store.extend(ajaxMixin ,{
		init() {
		    this._super(...arguments);
		  },
		 
			 
		  saveTaskSubmission: function( json) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
			 var url = "/rest/secure/task/submission" ;
			 this.doPost(url , json ).then((data ) =>{
					  resolve(data);
			      });
		 });
	 },
});		  