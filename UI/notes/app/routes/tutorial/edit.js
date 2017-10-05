import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin, {


    model(params) {
        return this.tutorialService.fetchTutorialById(params.tutorialId);

    },

    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Edit Tutorial');
        controller.set('buttonLabel', 'Save changes');
    },
    renderTemplate() {
        this.render('tutorial/upsert');
    },



    actions: {
        cancelClicked(tutorial) {
            this.transitionTo('tutorial.view', tutorial.id);
        },

        saveTutorial(tutorial) {
            var json = tutorial.getProperties.apply(tutorial, tutorial.dbFields);
            this.tutorialService.saveTutorial(json).then((resp) => {
            	 this.transitionTo('tutorial.view', tutorial.id);
              /*  if (resp.code === 0) {
                    this.transitionTo('tutorial.view', resp.item.id);
                } else if (resp.message) {
                    alert(resp.message);
                }*/
            });
        },
        willTransition(transition) {
            let model = this.controller.get('model');
            /*
             * if(model.get('hasDirtyAttributes')){ let confirmation =
             * confirm("leave without saving ? "); if(confirmation){
             * model.rollbackAttributes(); }else{ transition.abort(); } }
             */
        },
        error(reason){
        	this.transitionTo('dashboard');
        },
    }
});