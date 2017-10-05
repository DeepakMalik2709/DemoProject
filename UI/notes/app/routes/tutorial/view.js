import Ember from 'ember';

export default Ember.Route.extend({


    model(params) {
        return this.tutorialService.fetchTutorialById(params.tutorialId);

    },

    setupController: function(controller, model) {
        this._super(controller, model);
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
    },




    actions: {

        playVideo() {
            var model = this.controller.get("model");
            model.toggleProperty("showVideo");
        },
        confirmAndDeleteTutorial() {
            let confirmation = confirm('Are you sure?');

            if (confirmation) {
            	  var tutorial = this.controller.get("model");
            	 this.tutorialService.deleteTutorial(tutorial).then((resp) => {
                     if (resp.code === 0) {
                         this.transitionTo('home');
                     } else if (resp.message) {
                         alert(resp.message);
                     }
                 });
            }
        },
        error(reason){
        	this.transitionTo('dashboard');
        },
    }
});