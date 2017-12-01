import Ember from 'ember';
import ajaxMixin from '../../mixins/ajax';
import authenticationMixin from '../../mixins/authentication';
import instituteMixin from '../../mixins/institute';

export default Ember.Route.extend(ajaxMixin,authenticationMixin,instituteMixin, {


    model(params) {
        return this.store.findRecord('group', params.groupId);
    },
    groupService: Ember.inject.service('group'),
    init() {
	    this._super(...arguments);
	  },
    setupController: function(controller, model) {
        this._super(controller, model);
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        this.fetchGroupAttendanceMembers(model);
    },

    fetchGroupAttendanceMembers (){
    	var controller = this.get("controller");
    	controller.set("noRecords" , false);
    	if(typeof controller.get("members") == 'undefined'){
    		controller.set("members" , [])
		}
		var model = controller.get('model');
		controller.set("isLoading" , true);
		var	url = "/rest/secure/group/" + model.get("id") + "/attendance/members";
		this.doGet(url).then((result)=>{
			controller.set("isLoading" , false);
			if(result.code ==0){
				if(result.items && result.items.length){
					controller.get("members").pushObjects( result.items);
				}else{
					controller.set("noRecords" , true);
				}
			}else{
				controller.set("noRecords" , true);
			}
		})
    	
    },

    actions: {

        confirmAndDeleteGroup() {
        	var group = this.controller.get("model");
            let confirmation = confirm(`Are you sure you want to delete ${group.get("name")} ?`);

            if (confirmation) {
            	this.get("groupService.myGroups").removeObject(group); 
            	group.destroyRecord();
            	this.transitionTo('dashboard');
            }
        },
      
        saveGroupMembers(){
        	var members =  this.controller.get("newMembers");
        	var addedGroups =  this.controller.get("addedGroups");
        	if(members.length>0 || addedGroups.length > 0){
        		var model = this.controller.get('model');
        		var url =  "/rest/secure/group/" + model.id +"/members";
        		var json = {
        				members : members,
        				groups : addedGroups
        		}
        		this.doPost(url , json).then((result)=>{
        			  this.controller.set("newMembers", []);
        			  this.addMembersToGroup( result.item.members, addedGroups);
        			   this.send('showMembers');
        		});
        	}
        },
        error(reason){
        	this.transitionTo('dashboard');
        },
    }
});