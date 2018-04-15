import Ember from 'ember';

export default Ember.Route.extend({

	model(params) {
      /*  return {
			"id":123,
			"name":'Trees And Graphs',
			"subject":'Data Structures And Algorithms',
			"marks":50,
		    "question":[{"id":"101","no":1,"title":"Best Sorting algo for linked list?","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"102","no":2,"title":"Best Sorting algo for Array","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"101","no":1,"title":"Best Sorting algo for linked list?","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"102","no":2,"title":"Best Sorting algo for Array","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"101","no":1,"title":"Best Sorting algo for linked list?","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"102","no":2,"title":"Best Sorting algo for Array","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"101","no":1,"title":"Best Sorting algo for linked list?","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"102","no":2,"title":"Best Sorting algo for Array","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"101","no":1,"title":"Best Sorting algo for linked list?","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"102","no":2,"title":"Best Sorting algo for Array","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"101","no":1,"title":"Best Sorting algo for linked list?","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]},{"id":"102","no":2,"title":"Best Sorting algo for Array","options":[{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"},{"id":"1001","no":"A","title":"Bubble Sort"}]}]
		} */

    return this.store.find('quiz',params.quizId);
	},
	setupController: function(controller, model) {
        this._super(controller, model);
        //console.log(model.questions);
    let ques = model.get('questions')
    controller.set('selectedQuestion',ques[0]);

		controller.set('questionsList',ques);
		controller.set('isPrevDisabled',true);

    },

	actions: {

	    	 clickQuesionNo(question){
				 	    		   this.get('controller').set('selectedQuestion',question);
								   if(question.number==1){
									this.get('controller').set('isPrevDisabled',true);
								   }
	     		},
			  nextQuestion(){
				  var selectedIndex=this.get('controller').get('selectedQuestion.number');
				  var questionsList=this.get('controller').get('questionsList');
		          this.get('controller').set('selectedQuestion',questionsList[selectedIndex]);
                  if(selectedIndex+1==questionsList.length){
									this.get('controller').set('isNextDisabled',true);
									this.get('controller').set('isPrevDisabled',false);
								   }
                },
			  previousQuestion(){
				  var selectedIndex=this.get('controller').get('selectedQuestion.number');
				  var questionsList=this.get('controller').get('questionsList');
		          this.get('controller').set('selectedQuestion',questionsList[selectedIndex-2]);
                  if(selectedIndex==2){
				    this.get('controller').set('isPrevDisabled',true);
					this.get('controller').set('isNextDisabled',false);
				  }
                },

	    }
	});
