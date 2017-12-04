import Ember from 'ember';
import DS from 'ember-data';
import ajaxMixin from '../mixins/ajax';

export default DS.Store.extend(ajaxMixin ,{
		myGroups : null,
		init() {
		    this._super(...arguments);
		    this.set('myGroups', []);
		  },
		  
	 languages : [{id : "ENGLISH_AND_HINDI", label : "English and Hindi"} ,{id :  "ENGLISH", label : "English"} ,{id: "HINDI", label : "Hindi" }],
	 fetchMyGroups: function() {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
			 if(this.myGroups.length){
				  resolve(this.myGroups);
			 }else{
				var url = "/rest/secure/groups/mine";
				this.doGet(url).then((data ) =>{
					this.myGroups=[];
			    	  if(data.code ==0 && data.items){
			    		  for(var m =0; m<data.items.length;m++){
			    			  var record = data.items[m];
				    		  record.languagesUI = [];
				    		  for(var i =0; i < record.languages.length; i++){
				    			  var thisLang = record.languages[i];
				    			  for(var k =0; k< this.languages.length; k++){
				    				  var iterLang = this.languages[k];
				    				  if(iterLang.id == thisLang){
				    					  record.languagesUI.push(iterLang);
				    				  }
				    			  }
				    		  }
				    		  this.myGroups.pushObject(record);
			    		  }
			    	  }
			    	  resolve(this.myGroups);
			      });
			 }
		 });
	 },
	 fetchGroupPosts: function(group, nextPageLink ) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = nextPageLink ? nextPageLink : ( "/rest/secure/group/" + group.id + "/posts" );
				this.doGet(url).then((data ) =>{
					if(data.code ==0 && data.items){
						var items = [];
						for(var i =0;i<data.items.length; i++){
							var thisItem = data.items[i];
							let storePost1 = this.peekRecord('post', thisItem.id);
							if(storePost1){
								this.unloadRecord(storePost1);
							}
							 const storePost = this.createRecord('post', thisItem);
							 items.push(storePost);
						}
						
						resolve({code : 0 , items : items, nextLink : data.nextLink});
					}else{
						resolve(data);
					}
			    	  
			      });
		 });
	 },
	 fetchMyHomePosts: function( nextPageLink ) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = nextPageLink ? nextPageLink : ( "/rest/secure/my/posts" );
				this.doGet(url).then((data ) =>{
					if(data.code ==0 && data.items){
						var items = [];
						for(var i =0;i<data.items.length; i++){
							var thisItem = data.items[i];
							let storePost1 = this.peekRecord('post', thisItem.id);
							if(storePost1){
								this.unloadRecord(storePost1);
							}
							 const storePost = this.createRecord('post', thisItem);
							 items.push(storePost);
						}
						
						resolve({code : 0 , items : items, nextLink : data.nextLink});
					}else{
						resolve(data);
					}
			      });
		 });
	 },
	 toggleAdminMember: function(group, member , isAdmin) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/group/" + group.id + "/members/" + member.id + "/toggleAdmin?isAdmin=" + isAdmin;
				this.doPost(url).then((data ) =>{
			    	  resolve(data);
			      });
		 });
	 },
		
	 toggleBlockMember: function(group, member , isBlocked) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/group/" + group.id + "/members/" + member.id + "/toggleBlock?isBlocked=" + isBlocked;
				this.doPost(url).then((data ) =>{
			    	  resolve(data);
			      });
		 });
	 },	
	 deleteMember: function(group, member ) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/group/" + group.id + "/members/" + member.id ;
				this.doDelete(url).then((data ) =>{
			    	  resolve(data);
			      });
		 });
	 },
	 deleteMemberGroup: function(group, memberGroup ) {
		 return  new Ember.RSVP.Promise((resolve, reject) =>{
				var url = "/rest/secure/group/" + group.id + "/memberGroup/" + memberGroup.id ;
				this.doDelete(url).then((data ) =>{
			    	  resolve(data);
			      });
		 });
	 },
	 updateMember :  function( id, member) {
		    return new Ember.RSVP.Promise((resolve, reject) =>{
		    	let url = "/rest/secure/group/" +id + "/member/update";
		    	let json = {
	        			email : member.email,
	        			positions : [],
	        			id:member.id,
	        	}
		    	if(member.roles){
		    		for(var i =0; i<member.roles.length;i++){
		        		var role = member.roles[i];
		        		json.positions.push(role.id);
		        	}
		    	}
	        	
		    	this.doPost(url, json ).then(function(data) {
		    		if(data.code == 0){
		    			Ember.run(null, resolve, data);
		    		}else{
		    			  Ember.run(null, reject, jqXHR);
		    		}
		      }, function(jqXHR) {
		        jqXHR.then = null; // tame jQuery's ill mannered promises
		        Ember.run(null, reject, jqXHR);
		      });
		    });
		  },
});