import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Adapter.extend(ajaxMixin ,{
	 findAll: function(store, type, id, snapshot) {

		    return new Ember.RSVP.Promise((resolve, reject) =>{
		      this.doGet(`/rest/quiz/myQuiz`).then((data)=> {
						var record = [];
		    	  if(data.code ==0){
		    		  var record = data.items;
		    		  resolve(record);
		    	  }else{
		    		  resolve(record);
		    	  }
		      }, function(jqXHR) {
		        reject(jqXHR);
		      });
		    });
		  },
	findRecord(store, typeClass, id) {
		return new Ember.RSVP.Promise((resolve, reject) =>{
			this.doGet(`/rest/quiz/play/${id}`).then((data)=> {
				var record = [];
				if(data.code ==0){
					var record = data.items;
					resolve(record);
				}else{
					resolve(record);
				}
			}, function(jqXHR) {
				reject(jqXHR);
			});
		});
 		},


});
