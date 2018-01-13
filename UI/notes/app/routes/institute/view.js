import Ember from 'ember';
import ajaxMixin from '../../mixins/ajax';
import authenticationMixin from '../../mixins/authentication';
import instituteMixin from '../../mixins/institute';

export default Ember.Route.extend(ajaxMixin,authenticationMixin, instituteMixin, {

    model(params) {
        return this.store.findRecord('institute', params.instituteId);
    },
    instituteAdapter : null,
    hasMoreRecords : true,
    nextPageLink : null,
    joinRequestNextPageLink : null,
    hasMoreRequestRecords : true,
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
        this.controller.set("roles", this.roles);
        controller.set("noJoinRequests" , false);
         this.set('hasMoreRecords', true);
	    this.set('nextPageLink', null);
        this.fetchMembers();
        if(model.get( "bgImagePath")){
    		var bgImageSrc  = "/a/public/file/preivew?id=" + model.get( "bgImagePath")
    		 this.controller.set("bgImageSrc",bgImageSrc);
    	}
        this.instituteAdapter = this.store.adapterFor('institute');
    },

    fetchMembers (){
    	var controller = this.get("controller");
    	if(this.hasMoreRequestRecords && !controller.get("isLoading")){
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
    addMembersToGroup( list){
		let controller = this.get("controller");
    	let model = controller.get('model');
    	if(list && list.length){
	    	if(typeof model.get("members") == 'undefined'){
				model.set("members" , [])
			}
	    	this.cleanupMembers(list);
			model.get("members").pushObjects(list);
    	}
    	
    },

    actions: {
        showAddMemberModal(){
        	Ember.$("#members-add-modal").modal("show");
        	this.cleanupMembers(this.controller.get("model.members"));
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
        saveMembers(){
        	var members =  this.controller.get("newMembers");
        	if(members.length>0 ){
        		var model = this.controller.get('model');
        		var url =  "/rest/secure/institute/" + model.id +"/members";
        		var json = {
        				members : members,
        		}
        		this.doPost(url , json).then((result)=>{
        			  this.controller.set("newMembers", []);
        			  this.addMembersToGroup( result.item.members);
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
        fetchMoreMembers(){
        	var model = this.controller.get('model');
	         this.fetchMembers(model);
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
        deleteMember(member){
        	let confirmation = confirm("Remove user " + member.email +  "?");
        	if(confirmation){
	        	let model = this.controller.get('model');
	        	Ember.set(member, "isLoading" ,true);
	        	this.instituteAdapter.deleteMember(model.id , member ).then((result)=>{
	        		if (result.code == 0 ){
	        			if(model.get("members")){
	        				model.get("members").removeObject(member);
	        			}
	        			if(model.get("joinRequests")){
	        				model.get("joinRequests").removeObject(member);
	        			}
	        		}
	        	});
	        }
        },
        approveMember(member){
	        	let model = this.controller.get('model');
	        	
	        	this.instituteAdapter.approveMember(model.get("id") ,member );
	        	model.get("joinRequests").removeObject(member);
	        	model.get("members").pushObject(member);
	        	if(!model.get("joinRequests").length){
	        		Ember.$("#members-request-modal").modal("hide");
	        	}
        },
        updateMember(member){
        	let model = this.controller.get('model');
        	Ember.set(member, "isLoading" ,true);
        	Ember.set(member, "isUpdated" ,false);
        	this.instituteAdapter.updateMember(model.get("id") ,member ).then((result)=>{
        		Ember.set(member, "isLoading" ,false);
        	});
        	
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
        			this.instituteAdapter.toggleBlockMember(model.id , member , !member.isBlocked).then((result)=>{
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