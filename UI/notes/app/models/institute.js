import DS from 'ember-data';

export default DS.Model.extend({
	name: DS.attr('string'),
	  description: DS.attr('string'),
	  bgImageId: DS.attr('string'),
	  bgImagePath: DS.attr('string'),
	  bgImageFile :  DS.attr( {
		    defaultValue() { return {}; }
	  }),
	  icon: DS.attr('string'),
	  isMember: DS.attr('boolean'),
	  isBlocked: DS.attr('boolean'),
	  isAdmin: DS.attr('boolean'),
	  isSaving : DS.attr('boolean'),
	  isJoinRequestApproved :DS.attr('boolean'),
	  isJoinRequested :DS.attr('boolean'),
	  positions : DS.attr( {
		    defaultValue() { return []; }
	  }),
	  members: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  admins: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  isValidName: Ember.computed.notEmpty('name'),
	  showJoinButton :  Ember.computed('isJoinRequestApproved', 'isJoinRequested', function() {
		  if(this.get('isJoinRequestApproved')){
			  return false;
		  }else  if(this.get('isJoinRequested')){
			  return false;
		  }
		  return true;
		    
	  }),
	  isValid: Ember.computed('name', 'isSaving', function() {
		  if(this.get('isSaving')){
			  return false;
		  }
		    return !( typeof this.get('name') == 'undefined' || this.get('name').length <= 0 );
	  }),
	  
});
