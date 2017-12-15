import DS from 'ember-data';
import Ember from 'ember';

export default DS.Model.extend({
	dbFields : ["firstName", "lastName", "password" , "sendGroupPostEmail" ,
	            "sendGroupPostMentionEmail" , "sendPostCommentedEmail",
	            "sendCommentMentiondEmail" , "sendCommentOnMentiondPostEmail",
	            "sendCommentReplyEmail", "sendCommentOnCommentEmail"],
	lastName: DS.attr('string'),
	firstName: DS.attr('string'),
	password: DS.attr('string'),
	password2: DS.attr('string'),
	useGoogleDrive : DS.attr('boolean'),
	sendGroupPostEmail : DS.attr('boolean'),
	sendGroupPostMentionEmail : DS.attr('boolean'),
	sendPostCommentedEmail : DS.attr('boolean'),
	sendCommentMentiondEmail : DS.attr('boolean'),
	sendCommentOnMentiondPostEmail : DS.attr('boolean'),
	sendCommentReplyEmail : DS.attr('boolean'),
	sendCommentOnCommentEmail : DS.attr('boolean'),
	googleDriveMsgDate : DS.attr('number'),
	refreshTokenAccountEmail: DS.attr('string'),
	institutes: DS.attr( {
		    defaultValue() { return []; }
		  }),
	instituteMembers: DS.attr( {
	    defaultValue() { return []; }
	  }),
	  isValidFirstName: Ember.computed.notEmpty('firstName'),
	  isValidLastName: Ember.computed.notEmpty('lastName'),
	  isPasswordNotEmpty :  Ember.computed.notEmpty('password'),
	  doPasswordsNotMatch :  Ember.computed('password', 'password2', function() {
		    return (this.get('password') != this.get('password2'));
	  }),
	  
	  isInValidPassword : Ember.computed.and('isPasswordNotEmpty' , 'doPasswordsNotMatch'),
	  isValidPassword : Ember.computed.not('isInValidPassword'),
	  isValid : Ember.computed.and("isValidFirstName","isValidLastName","isValidPassword"),
	  
	  
	  save: function(){
	      var data = JSON.stringify(this.getProperties.apply(this, this.dbFields));
	      return Ember.$.when(
					Ember.$.ajax({
					"url" : '/rest/secure/profile',
					method : "POST",
					contentType : "application/json;charset=UTF-8",
					data : data
					})
				);
	    }
});
