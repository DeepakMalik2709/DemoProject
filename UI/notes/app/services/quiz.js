import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Store.extend(ajaxMixin,{
	 languages : [{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	items : [],
	init() {
		 this._super(...arguments);
		    this.set('items', []);
	  },
	  updateResponse: function(json) {
			 return  new Ember.RSVP.Promise((resolve, reject) =>{
					var url = "/rest/calendar/updateQuiz";
					this.doPost(url , json ).then((data ) =>{
						  resolve(data);
				      });
			 });
		 },

	saveQuiz:function(json){
		return  new Ember.RSVP.Promise((resolve, reject) =>{
			 var url = "/rest/quiz/upsert";
			 this.doPost(url , json ).then((data ) =>{
					 resolve(data);
					 });
		});
	},
	deleteQuiz:function(quiz){
		return this.doDelete("/rest/secure/quiz/" + quiz.id);
	},
	searchQuiz:function(term , nextLink) {
		var url = "/rest/public/quiz/search?q="+ term ;
		if(nextLink){
			url = nextLink;
		}
		return this.get(url);
	},
	fetchTrending:function( nextLink) {
		var url = "/rest/public/trending" ;
		if(nextLink){
			url = nextLink;
		}
		return this.get(url);
	},
	fetchMyQuizList:function(){
		return this.get("/rest/secure/calendar/quiz" );
	}

});
