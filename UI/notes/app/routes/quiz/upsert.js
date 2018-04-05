import Ember from 'ember';

export default Ember.Route.extend({

	  selectedGroups: [],
		myGroups:[],
		quesService: Ember.inject.service('question'),
		groupService: Ember.inject.service('group'),
    contextService: Ember.inject.service('context'),
	model() {
        return this.store.createRecord('quiz');
    },

	setupController: function(controller, model) {
        this._super(controller, model);

				let request = this.get('groupService').fetchMyGroups();
						request.then((response) => {
							var adminGroups =(false)? response.filterBy("isAdmin", true) : response;

							if(adminGroups.length){
								this.set("myGroups" ,adminGroups );
							}else{
								alert("You can quiz to groups where you are admin");
							}
						});

						let quesRequest = this.get('quesService').fetchmyQuestions();
						quesRequest.then((response) => {
								this.set("myQuestions",response);
						});
    },

    actions: {
			upsertQuiz(event) {
				if(this.validation(event)){
					this.set("submitted", true);
					var selectedGroups =  this.get("selectedGroups");
					for(var i =0 ;i < selectedGroups.length ;i++){
					Ember.get(event,"groupIds").pushObject(selectedGroups[i].id);
				}

						Ember.set(event, "showLoading", true);
						event.weekdays=[];
						this.selectedDay.forEach(function(item, index) {
							if(!event.weekdays.isAny('value', item.value)){
					event.weekdays.push(item.value);
				}
			});
						// console.log(event.toJSON());
						 this.sendAction('saveEvent', event);

				}
			}
	}
});
