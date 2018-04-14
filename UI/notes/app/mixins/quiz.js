import Ember from 'ember';
import utilsMixin from './utils';

export default Ember.Mixin.create(utilsMixin , {
	contextService: Ember.inject.service('context'),
	postService: Ember.inject.service('post'),
	canEdit :Ember.computed('item', 'contextService','group', function() {
		if(this.get('contextService.context.loginUser')){
			return this.get('item.createdByEmail') ==  this.get('contextService.context.loginUser.email');
		}
		return false;
	  }),
	  canDelete :Ember.computed('item', 'contextService', function() {
			if(typeof this.get('group') !=undefined){
				if(this.get('group.isAdmin')){
					return true;
				}else if(this.get('contextService.context.loginUser')){
					return this.get('item.createdByEmail') ==  this.get('contextService.context.loginUser.email');
				}else if(this.get('group.isBlocked')){
					return false;
				}else if(!this.get('group.isMember')){
					return false;
				}
			}
			if(this.get('contextService.context.loginUser')){
				return this.get('item.createdByEmail') ==  this.get('contextService.context.loginUser.email');
			}
			return false;
		  }),
	   cleanupPost(item) {
			if(item.createdByEmail){
				let photoUrl = this.getPhotoUrl(Ember.get(item, 'createdByEmail'));
				Ember.set(item, "photoUrl", photoUrl);
				if(!item.comments){
					/*for(var i =0; i< item.comments.length;i++){
						var thisComment = item.comments[i];
						this.cleanupTimes(thisComment);
						photoUrl = this.getPhotoUrl(thisComment.createdByEmail);
						Ember.set(thisComment, "photoUrl", photoUrl);
					}
				}else{*/
					Ember.set(item, "comments", []);
				}
			}
			this.cleanupTimes(item);
		},
		updateRecipients(item){
			var html = "<div>" +  Ember.get(item , "comment")  + "</div>";
			var arr = [];
			$(html).find("[data-email]").each(function(elem){
				if(!$(this).hasClass("inactive")){
					var email = $(this).attr("data-email");
					arr.push({email : email, label : email});
				}
			});
			Ember.set(item, "recipients", arr);
		},
		myList : [{email : 'jitender@nicefact.co.in' , label:'jitender'}, {email : 'harish@nicefact.co.in', label : 'harish'}],
});
