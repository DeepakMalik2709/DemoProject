import Ember from 'ember';
import moment from 'moment';
import authenticationMixin from '../mixins/authentication';

export default Ember.Route.extend(authenticationMixin , {
	profileService: Ember.inject.service('profile'),
    model(params) {
    	var context = this.contextService.fetchContext((result)=>{
			let certificates = [];

			var _this = this;

			result.certificates.forEach(function(element) {
				let certificate = _this.store.createRecord('certificate', {
					certificateId: element.certificateId,
					name: element.name,
					date: element.date,
					image: element.image,
					organisation: element.organisation,
					grade: element.grade,
					appUserId: element.appUserId
				});

				certificates.push(certificate);
			});

    		var user = this.store.createRecord('user', {
				id: result.loginUser.id, 
        		lastName: result.loginUser.lastName,
        		firstName: result.loginUser.firstName, 
        		email : result.loginUser.email,
        		photoUrl : result.loginUser.photoUrl,
        		hasUploadedPhoto :  result.loginUser.hasUploadedPhoto,
        		useGoogleDrive : result.loginUser.useGoogleDrive,
        		useGoogleCalendar : result.loginUser.useGoogleCalendar,
        		refreshTokenAccountEmail : result.loginUser.refreshTokenAccountEmail,
        		sendGroupPostEmail : result.loginUser.sendGroupPostEmail  ,
        		sendGroupPostMentionEmail: result.loginUser.sendGroupPostMentionEmail  ,
        		sendPostCommentedEmail : result.loginUser.sendPostCommentedEmail  ,
        		sendCommentMentiondEmail: result.loginUser.sendCommentMentiondEmail  ,
        		sendCommentOnMentiondPostEmail : result.loginUser.sendCommentOnMentiondPostEmail  ,
        		sendCommentReplyEmail : result.loginUser.sendCommentReplyEmail  ,
        		sendCommentOnCommentEmail : result.loginUser.sendCommentOnCommentEmail  ,
        		institutes : result.institutes,
				instituteMembers : result.instituteMembers,
				certificates: certificates
        		});
    		return user;
    	});
    	
    	return context;
    },
    
    
    init: function() {
    	 
    },
    setupController: function(controller, model) {
        this._super(controller, model);
        var model = this.controller.get('model');
   	 	
        this.controller.set("isLoggedIn", this.controllerFor("application").get("isLoggedIn"));
        controller.set("isSearchButtonDisabled", Ember.computed.empty("model.searchTerm"));
    },
    
    actions: {
        saveProfile() {
        	if(!this.get("controller").get("isSaving")){
	            var model = this.get("controller").get("model");
	            this.get("controller").set("isSaving", true);
	            model.save().then(result=>{
	            	this.controller.set("isSaving", false);
	            	if(result.code==0){
	            		this.contextService.setLoginUser(result.item);
	            		this.transitionTo('home');
	            	}
	            });
        	}
        },
        
        removePhoto : function(){
        	this.get("profileService").removePhoto().then((result)=>{
        		this.controller.get('model').set('photoUrl', '/img/users/user_1.jpg');
	    	});
        },

        saveInstitutesInformation : function(instituteMembers){
        	this.get("profileService").saveInstitutesInformation(instituteMembers).then((result)=>{
        		if(result.code === 0){
        			alert(result.item.organization+" information has saved successfully");
 				}else{
 					alert(result.item.organization+" information has not saved successfully. Please try again.");
 				}
        	});
        },
        
        toggleGoogleDrive : function(){
        	if(!this.get("controller").get("isSaving")){
        		this.get("controller").set("isSaving", true);
	        	var model = this.controller.get('model');
	        	model.toggleProperty('useGoogleDrive');
	        	if(model.get('useGoogleDrive')){
	        		window.location.href= "/a/oauth/driveAuthorization";
	        	}else{
	        		this.get("profileService").deauthorizeGoogleDrive().then(result=>{
	        			this.get("controller").set("isSaving", false);
	        			model.set('useGoogleCalendar', false);
	        		});
	        	}
        	}
        },
        toggleGoogleCalendar : function(){
        	if(!this.get("controller").get("isSaving")){
        		this.get("controller").set("isSaving", true);
	        	var model = this.controller.get('model');
	        	model.toggleProperty('useGoogleCalendar');
	        	if(model.get('useGoogleCalendar')){
	        		window.location.href= "/a/oauth/calendarAuthorization";
	        	}else{
	        		this.get("profileService").deauthorizeGoogleCalendar().then(result=>{
	        			this.get("controller").set("isSaving", false);
	        			model.set('useGoogleDrive', false);
	        		});
	        	}
        	}
        },
        toggleValue : function(attribute){
        	var model = this.controller.get('model');
        	model.toggleProperty(attribute);
        },
        addInstituteBtnClick:function(){
        	Ember.$("#add-institute-modal").modal("show");
        	this.get("controller").set("searchInstitutes", []);
        	this.get("controller").set("instituteSearchTerm", '');
        	this.controller.set( "showLoadingInstitutes", false);
			this.controller.set("noInstitutes", false);
        },
        createNewInstitute:function(){
        	this.transitionTo('institute.create' , { queryParams: { name: this.get("controller").get("instituteSearchTerm") }});
        	
        	// 
        },
        searchInstitutes:function(){
        	this.controller.set( "showLoadingInstitutes", true);
			this.controller.set("noInstitutes", false);
        	this.get("controller").set("searchInstitutes", []);
        	const adapter = this.store.adapterFor('institute');
 			adapter.searchByName(this.get("controller").get("instituteSearchTerm")).then((resp) => {
 				this.controller.set( "showLoadingInstitutes", false);
 				if(resp.length){
 					this.get("controller").set("searchInstitutes", resp);
 				}else{
 					this.controller.set("noInstitutes", true);
 				}
	    			
	    		});
        },
        joinInstituteClick(institute){
        		const instituteAdapter = this.store.adapterFor('institute');
        		instituteAdapter.joinInstitute(institute.id);
        		alert("your request has been sent for approval.")
		},
		
		updateProfileInfo(model) {
			this.get("profileService").updateProfileInfo(model).then(result=>{
				this.controller.get('model').set('firstName', result.item.firstName);
				this.controller.get('model').set('lastName', result.item.lastName);
				this.controller.get('model').set('currentCity', result.item.currentCity);
				this.controller.get('model').set('phoneNumber', result.item.phoneNumber);
				this.controller.get('model').set('about', result.item.about);

				this.contextService.setLoginUser(result.item);
				Ember.$('#profile-info').modal('hide');
			});
		},

		updatePersonalInfo(model) {
			this.get("profileService").updatePersonalInfo(model).then(result=>{
				this.controller.get('model').set('dob', result.item.dob);
				this.controller.get('model').set('homeTown', result.item.homeTown);
				
				this.contextService.setLoginUser(result.item);
				Ember.$('#personal-info').modal('hide');
			});
		},
		
		updateCertificate(model) {
			this.get("profileService").updateCertificate(model).then(result=>{
				let certificates = this.controller.get('model').get('certificates');
				let certificate = certificates.find(certificate => certificate.get('certificateId') == model.get('certificateId'));

				certificate.set('name', result.item.name);
				certificate.set('date', result.item.date);
				certificate.set('image', result.item.image);
				certificate.set('organisation', result.item.organisation);
				certificate.set('grade', result.item.grade);

				Ember.$('#certificate-'+model.get('certificateId')).modal('hide');
			});
		},
		
		saveCertificate(model) {
			model.set("appUserId", this.controller.get('model').get("id"));

			this.get("profileService").saveCertificate(model).then(result=>{
				let newCertificate = this.store.createRecord('certificate', {
					certificateId: result.item.id,
					name: result.item.name,
					date: result.item.date,
					image: result.item.image,
					organisation: result.item.organisation,
					grade: result.item.grade
				});

				this.controller.get('model').get('certificates').pushObject(newCertificate);
				Ember.$('#certificate-0').modal('hide');
			});
		},
		
		deleteCertificate(index) {
			let certificates = this.controller.get('model').get('certificates');

			let certificate = certificates.objectAt(index);

			Ember.$('#delete-certificate-'+certificate.get('certificateId')).modal('hide');

			this.get("profileService").deleteCertificate(certificate.get('certificateId')).then(result=>{
				this.controller.get('model').get('certificates').removeAt(index);
			});
		}
    }
});