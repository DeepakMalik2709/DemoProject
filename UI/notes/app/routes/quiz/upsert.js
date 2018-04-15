import Ember from 'ember';

export default Ember.Route.extend({

		notifications: Ember.inject.service('notification-messages'),
		quizService: Ember.inject.service('quiz'),
		quesService: Ember.inject.service('question'),
		groupService: Ember.inject.service('group'),
    contextService: Ember.inject.service('context'),
	model(params) {
        return this.store.find('quiz',params.quizId);
    },
		init() {
 		 this._super(...arguments);
 	 },
	setupController: function(controller, model) {
        this._super(controller, model);
 				controller.set("selectedGroups", []);
				let request = this.get('groupService').fetchMyGroups();
						request.then((response) => {
							var adminGroups =(false)? response.filterBy("isAdmin", true) : response;

							if(adminGroups.length){
								controller.set("myGroups" ,adminGroups );
							}else{
								alert("You can quiz to groups where you are admin");
							}
						});

						let quesRequest = this.get('quesService').fetchmyQuestions();
						quesRequest.then((response) => {
								controller.set("myQuestions",response);
						});
    },

    actions: {
			upsertQuiz(quiz,selGrp,selQue) {
				 console.log(quiz.toJSON());

					for(var i =0 ;i < selGrp.length ;i++){
						quiz.get("groups").pushObject(selGrp[i].id);
					}
					for(var i =0 ;i < selQue.length ;i++){
						quiz.get("questions").pushObject({"id":selQue[i].id});
					}
 				this.get('quizService').saveQuiz(quiz).then((result)=>{
							if(result.code == 0){
								this.get('notifications').setDefaultAutoClear(true);
							 this.get('notifications').info('Quiz saved successfully.');
									this.transitionTo('quiz.grid');
						}else{
							this.get('notifications').setDefaultAutoClear(true);
						 this.get('notifications').info('Oop some error ! Please contact to admin .');

						}
					});
			}
		}

});
