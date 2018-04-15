import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Store.extend(ajaxMixin ,{
		myQuestions : null,
		init() {
		    this._super(...arguments);
		    this.set('myQuestions', []);
		  },

	 fetchmyQuestions: function() {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{

				var url = "/rest/question/getAll";
				this.doGet(url).then((data ) =>{
					this.myQuestions=[];
			    	  if(data.code ==0 && data.items){
			    		  for(var m =0; m<data.items.length;m++){
			    			  var record = data.items[m];
				    		  this.myQuestions.pushObject(record);
			    		  }
							}
			    	  resolve(this.myQuestions);
			      });			 
		 });
	 }
});
