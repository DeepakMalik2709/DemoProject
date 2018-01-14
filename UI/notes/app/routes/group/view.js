import Ember from 'ember';
import ajaxMixin from '../../mixins/ajax';
import authenticationMixin from '../../mixins/authentication';
import instituteMixin from '../../mixins/institute';

export default Ember.Route.extend(ajaxMixin,authenticationMixin,instituteMixin, {


    model(params) {
        return this.store.findRecord('group', params.groupId);
    },
/*    afterModel(group, transition) {
        if (group) {
        	//this.fetchMembers();
        }
      },*/
    hasMoreRecords : true,
    nextPageLink : null,
    groupService: Ember.inject.service('group'),
    groupAdapter :null,
    blockQueue : null,
    adminQueue : null,
    joinRequestNextPageLink : null,
    hasMoreRequestRecords : true,
    init() {
	    this._super(...arguments);
	    this.set('blockQueue', []);
	    this.set('adminQueue', []);
	    this.set('hasMoreRecords', true);
	    this.set('nextPageLink', null);
	  },
    setupController: function(controller, model) {
        this._super(controller, model);
        this.groupAdapter = this.store.adapterFor('group');
        this.controller.set("roles", this.roles);
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        this.controller.set("newMembers", []);
        controller.set("noJoinRequests" , false);
         this.set('hasMoreRecords', true);
	    this.set('nextPageLink', null);
	    this.fetchMembers(model);
    },
    fetchMembers (group){
    	var controller = this.get("controller");
    	if(this.hasMoreRecords && !controller.get("isLoading")){
    		
    		var model = controller.get('model');
    		controller.set("isLoading" , true);
    		var url = this.nextPageLink;
    		if(!url){
    			url = "/rest/secure/group/" + group.get("id") + "/members";
    		}
    		this.doGet(url).then((result)=>{
    			controller.set("isLoading" , false);
    			if(result.code ==0){
    				if(result.items){
    					this.addMembersToGroup( result.items);
    					if(!Ember.get(group,"memberGroups")){
    						Ember.set(group, "memberGroups" ,result.memberGroups);
    					}
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

    addMembersToGroup( list, addedGroups){
		let controller = this.get("controller");
    	let model = controller.get('model');
    	if(list && list.length){
	    	if(typeof model.get("members") == 'undefined'){
				model.set("members" , [])
			}
	    	this.cleanupMembers(list);
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
    fetchJoinRquests (){
    	var controller = this.get("controller");
    	if(this.hasMoreRequestRecords && !controller.get("isLoading")){
    		
    		var model = controller.get('model');
    		controller.set("isLoading" , true);
    		var url = this.joinRequestNextPageLink;
    		if(!url){
    			url = "/rest/secure/group/" + model.get("id") + "/joinRequests";
    		}
    		this.doGet(url).then((result)=>{
    			controller.set("isLoading" , false);
    			if(result.code ==0){
    				if(result.items && result.items.length){
    					this.cleanupMembers(result.items);
    					Ember.set(model, "joinRequests" ,result.items)
    				}else{
    					controller.set("noJoinRequests" , true);
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
        addGroup(group1){
        	var addedGroups = this.controller.get("addedGroups");
        	var index = addedGroups.indexOf(group1);
        	if(index < 0){
        		addedGroups.pushObject(group1);
        	}
        },
        removeGroup(group1) {
        	var addedGroups = this.controller.get("addedGroups");
        	addedGroups.removeObject(group1);
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
	                 this.fetchMembers(model);
			}
        	Ember.$("#members-list-modal").modal("show");
        },
        fetchMoreMembers(){
        	var model = this.controller.get('model');
	         this.fetchMembers(model);
        },
        error(reason){
        	this.transitionTo('dashboard');
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
        deleteGroupMember(memberGroup){
        	let confirmation = confirm("Remove sub group " + memberGroup.name +  "?");
        	if(confirmation){
	        	let model = this.controller.get('model');
	        	Ember.set(memberGroup, "isLoading" ,true);
	        	this.get('groupService').deleteMemberGroup(model , memberGroup ).then((result)=>{
	        		if (result.code == 0 ){
	        			model.memberGroups.removeObject(memberGroup);
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
        },
        updateMember(member){
        	let model = this.controller.get('model');
        	Ember.set(member, "isLoading" ,true);
        	Ember.set(member, "isUpdated" ,false);
        	this.get('groupService').updateMember(model.get("id") ,member ).then((result)=>{
        		Ember.set(member, "isLoading" ,false);
        	});
        	
	    },
   copyPublicLink(){
	    	let model = this.controller.get('model');
	    	var context = this.contextService.fetchContext((result)=>{
	    		copyToClipboard(result.url + "/group/" + model.id)
	    		alert("Public link copied to clipboard");
	    	});
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
        approveMember(member){
        	let model = this.controller.get('model');
        	this.groupAdapter.approveMember(model.get("id") ,member );
        	model.get("joinRequests").removeObject(member);
        	model.get("members").pushObject(member);
        	if(!model.get("joinRequests").length){
        		Ember.$("#members-request-modal").modal("hide");
        	}
        },
        }
});