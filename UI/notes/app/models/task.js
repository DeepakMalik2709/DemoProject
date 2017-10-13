import DS from 'ember-data';

export default DS.Model.extend({
	  comment: DS.attr('string'),
	  groupId : DS.attr('number'),
	  groupName: DS.attr('string'),
	  createdByEmail: DS.attr('string'),
	  createdTime: DS.attr('number'),
	  updatedTime: DS.attr('number'),
	  deadlineTime: DS.attr('number'),
	  updatedByName: DS.attr('string'),
	  createdByName: DS.attr('string'),
	  updatedByEmail: DS.attr('string'),
	  isSaving : DS.attr('boolean'),
	  isTask :  DS.attr( {
		    defaultValue() { true }
	  }),
	  groupIds :DS.attr( {
		    defaultValue() { return []; }
	  }),
	  files: DS.attr( {
		    defaultValue() { return []; }
		  }),
	recipients: DS.attr( {
	    defaultValue() { return []; }
	  }),
	comments: DS.attr( {
	    defaultValue() { return []; }
	  }),
	numberOfComments: DS.attr('number'),
	  isValid: Ember.computed('comment', 'isSaving', function() {
		  if(this.get('isSaving')){
			  return false;
		  }
		    return !( typeof this.get('comment') == 'undefined' || this.get('comment').length < 3 );
	  }),
});
