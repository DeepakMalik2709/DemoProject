import DS from 'ember-data';

export default DS.Model.extend({
	name: DS.attr('string'),
	  description: DS.attr('string'),
	  sharing: DS.attr('string'),
	  bgImageId: DS.attr('string'),
	  icon: DS.attr('string'),
	  isMember: DS.attr('boolean'),
	  isBlocked: DS.attr('boolean'),
	  isAdmin: DS.attr('boolean'),
	  isSaving : DS.attr('boolean'),
	  members: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  tags: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  admins: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  languagesUI: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  isValidName: Ember.computed.notEmpty('name'),
	  isValidSharing: Ember.computed.notEmpty('sharing'),
	  isValid: Ember.computed('name', 'isSaving', function() {
		  if(this.get('isSaving')){
			  return false;
		  }
		    return !( typeof this.get('name') == 'undefined' || this.get('name').length <= 0 );
	  }),
	  
	 // createdBy: DS.attr('long'),
	//  createdTime: DS.attr('long'),
	 // lastModifiedTime: DS.attr('long'),
	 //
});
