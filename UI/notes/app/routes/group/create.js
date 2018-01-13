import Ember from 'ember';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(authenticationMixin, {


    model() {
        return this.store.createRecord('group');

    },
    groupService: Ember.inject.service('group'),
    contextService: Ember.inject.service('context'),
    afterModel(transition) {
		 var context = this.contextService.fetchContext(result=>{
				if(result && result.code==0){
					 var useGoogleDrive = Ember.get(result , "loginUser.useGoogleDrive")
					 if(false == useGoogleDrive){
			        		if(confirm("AllSchool needs access to Google Drive and Calendar to create Groups. Would you like to grant permission now ?")){
			        			window.location.href= "/a/oauth/googleAllAuthorization";
			        		}else{
								 this.transitionTo('home');
							 }
			        	}
				}
			});
		  },
   
    setupController: function(controller, model) {
        this._super(controller, model);
        controller.set('pageTitle', 'Create Group');
        controller.set('buttonLabel', 'Create');
        var context = this.contextService.fetchContext((result)=>{
        	 controller.set('institutes', result.institutes);
        });
    },
    renderTemplate() {
        this.render('group/upsert');
    },



    actions: {


        cancelClicked(tutorial) {
            this.transitionTo('index');
        },

        saveGroup(group) {
        	group.set("isSaving", true);
        	group.save().then((resp) => {
        		this.get("groupService.myGroups").pushObject(resp); 
        		$.event.trigger( "sidebarUpdated");
        		group.set("isSaving", false);
        		this.transitionTo('group.view', resp.id);
            });
        },
        willTransition(transition) {
            let model = this.controller.get('model');
            /*
             * if(model.get('hasDirtyAttributes')){ let confirmation =
             * confirm("leave without saving ? "); if(confirmation){
             * model.rollbackAttributes(); }else{ transition.abort(); } }
             */
        }
    }
});