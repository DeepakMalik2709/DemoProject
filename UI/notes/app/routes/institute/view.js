import Ember from 'ember';
import ajaxMixin from '../../mixins/ajax';
import authenticationMixin from '../../mixins/authentication';

export default Ember.Route.extend(ajaxMixin,authenticationMixin, {


    model(params) {
        return this.store.findRecord('institute', params.instituteId);
    },
    hasMoreRecords : true,
    nextPageLink : null,
    joinRequestNextPageLink : null,
    hasMoreRequestRecords : true,
    groupService: Ember.inject.service('group'),
    blockQueue : null,
    adminQueue : null,
    init() {
	    this._super(...arguments);
	    this.set('blockQueue', []);
	    this.set('adminQueue', []);
	    this.set('hasMoreRecords', true);
	    this.set('nextPageLink', null);
	  },
    setupController: function(controller, model) {
        this._super(controller, model);
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        this.controller.set("newMembers", []);
         this.set('hasMoreRecords', true);
	    this.set('nextPageLink', null);
        this.fetchMembers();
        if(model.get( "bgImagePath")){
    		var bgImageSrc  = "/a/public/file/preivew?id=" + model.get( "bgImagePath")
    		 this.controller.set("bgImageSrc",bgImageSrc);
    	}
    },

    fetchMembers (){
    	var controller = this.get("controller");
    	if(this.hasMoreRecords && !controller.get("isLoading")){
    		
    		var model = controller.get('model');
    		controller.set("isLoading" , true);
    		var url = this.nextPageLink;
    		if(!url){
    			url = "/rest/secure/institute/" + model.get("id") + "/members";
    		}
    		this.doGet(url).then((result)=>{
    			controller.set("isLoading" , false);
    			if(result.code ==0){
    				if(result.items){
    					this.addMembersToGroup( result.items);
    					Ember.set(model, "memberGroups" ,result.memberGroups)
    				}
    				if(result.nextLink){
    					this.set("nextPageLink", result.nextLink);
    				}else{
    					this.set("nextPageLink", null);
    					this.set("hasMoreRecords", false);
    				}
    				
    			}
    		})
    	}
    	
    },
    fetchJoinRquests (){
    	var controller = this.get("controller");
    	if(this.hasMoreRecords && !controller.get("isLoading")){
    		
    		var model = controller.get('model');
    		controller.set("isLoading" , true);
    		var url = this.joinRequestNextPageLink;
    		if(!url){
    			url = "/rest/secure/institute/" + model.get("id") + "/joinRequests";
    		}
    		this.doGet(url).then((result)=>{
    			controller.set("isLoading" , false);
    			if(result.code ==0){
    				if(result.items){
    					Ember.set(model, "joinRequests" ,result.items)
    				}
    				if(result.nextLink){
    					this.set("joinRequestNextPageLink", result.nextLink);
    				}else{
    					this.set("joinRequestNextPageLink", null);
    					this.set("hasMoreRequestRecords", false);
    				}
    				
    			}
    		})
    	}
    	
    },
    addMembersToGroup( list, addedGroups){
		let controller = this.get("controller");
    	let model = controller.get('model');
    	if(list && list.length){
	    	if(typeof model.get("members") == 'undefined'){
				model.set("members" , [])
			}
			model.get("members").pushObjects(list);
    	}
    	
    	if(addedGroups && addedGroups.length){
    		if(typeof model.get("memberGroups") == 'undefined'){
				model.set("memberGroups" , [])
			}
	    	 for(var k =0;k<addedGroups.length;k++){
	    		 let thisGroup = addedGroups[k];
	    		 let isExists = model.get("memberGroups").filterBy("id", Ember.get(thisGroup, "id")).length;
	    		 if(!isExists){
	    			 model.get("memberGroups").pushObject({ id : thisGroup.id , name : thisGroup.label });
	    		 }
	    		 
			  }
    	}
    },

    actions: {

        showAddMemberModal(){
        	Ember.$("#members-add-modal").modal("show");
        	if(this.get('groupService').myGroups){
        		var thisId = this.controller.get("model.id");
        		var myOwnedGroups = [];
        		var myGroups = this.get('groupService').myGroups;
        		
        		let myGroup;
        		for(var i =0;i<myGroups.length; i++){
        			myGroup = myGroups[i];
        			if(myGroup.isAdmin && myGroup.id != thisId){
        				myOwnedGroups.push({id : myGroup.id , label : Ember.get(myGroup, "name")});
        			}
        		}
        		this.controller.set('myOwnedGroups', myOwnedGroups);
        		this.controller.set('addedGroups', []);
        	}
        	 
        },
        addMember(){
        	var searchTerm = this.controller.get("userSearchTerm");
        	if( searchTerm.match( /^.+@.+\..+$/)){
        		 this.controller.get("newMembers").pushObject({email : searchTerm});
        		 this.controller.set("userSearchTerm" , "");
        	}
        },
        removeMember(member) {
            var membersList = this.controller.get("newMembers");
            membersList.removeObject(member);
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
        showMembers(){
        	var model = this.controller.get('model');
	    	if(typeof model.get("members") == 'undefined' || model.get("members").length <=0){
	    		  this.set('hasMoreRecords', true);
	         	    this.set('nextPageLink', null);
	                 this.fetchMembers();
			}
        	Ember.$("#members-list-modal").modal("show");
        },
        showJoinRquests(){
        	var model = this.controller.get('model');
	    	if(typeof model.get("joinRequests") == 'undefined' || model.get("joinRequests").length <=0){
	    		  this.set('hasMoreRequestRecords', true);
	         	    this.set('joinRequestNextPageLink', null);
	                 this.fetchJoinRquests();
			}
        	Ember.$("#members-request-modal").modal("show");
        },
        error(reason){
        	this.transitionTo('dashboard');
        },
        toggleUserList(){
        	 this.controller.get('model').toggleProperty("showGroupMembers");
        	return false;
        },
        deleteMember(member){
        	let confirmation = confirm("Remove user " + member.email +  "?");
        	if(confirmation){
	        	let model = this.controller.get('model');
	        	Ember.set(member, "isLoading" ,true);
	        	this.get('groupService').deleteMember(model , member ).then((result)=>{
	        		if (result.code == 0 ){
	        			model.get("members").removeObject(member);
	        		}
	        	});
	        }
        },
        toggleAdminMember(member){
        	var queue =  this.get('adminQueue');
        	let isQueued = queue.findBy( "id" , member.id);
        	Ember.set(member, "isLoading" ,true);
        	if(isQueued){
        		Ember.run.cancel(isQueued.timer);
        		queue.removeObject(isQueued);
        		Ember.set(member, "isLoading" ,false);
        	}else{
        		var timer = Ember.run.later(()=>{
		        	let model = this.controller.get('model');
		        	this.get('groupService').toggleAdminMember(model , member , !member.isAdmin).then((result)=>{
		        		Ember.set(member, "isLoading" ,false);
		        		if (result.code == 0 && result.item){
		        			var updatedMember = result.item;
		        			Ember.set(member, "isAdmin" , updatedMember.isAdmin);
		        		}
		        	});
		        	isQueued = queue.findBy( "id" , member.id);
                	queue.removeObject(isQueued);
        		} , 500);
        		queue.pushObject({"id" : member.id , "timer" : timer});
        	}
       },
        toggleBlockMember(member){
        	var queue =  this.get('blockQueue');
        	let isQueued = queue.findBy( "id" , member.id);
        	Ember.set(member, "isLoading" ,true);
        	if(isQueued){
        		Ember.run.cancel(isQueued.timer);
        		queue.removeObject(isQueued);
        		Ember.set(member, "isLoading" ,false);
        	}else{
        		var timer = Ember.run.later(()=>{
        			let model = this.controller.get('model');
                	this.get('groupService').toggleBlockMember(model , member , !member.isBlocked).then((result)=>{
                		Ember.set(member, "isLoading" ,false);
                		if (result.code == 0 && result.item){
                			var updatedMember = result.item;
                			Ember.set(member, "isBlocked" , updatedMember.isBlocked);
                		}
                	});
                	isQueued = queue.findBy( "id" , member.id);
                	queue.removeObject(isQueued);
            		} , 500);
        		queue.pushObject({"id" : member.id , "timer" : timer});
        	}
        }
    }
});