import DS from 'ember-data';

export default DS.Model.extend({
	  comment: DS.attr('string'),
	  groupId : DS.attr('number'),
	  groupName: DS.attr('string'),
	  createdByEmail: DS.attr('string'),
	  createdTime: DS.attr('number'),
	  updatedTime: DS.attr('number'),
	  updatedByName: DS.attr('string'),
	  createdByName: DS.attr('string'),
	  updatedByEmail: DS.attr('string'),
	  postType:DS.attr('string'),
	  postPriv:DS.attr('string'),
	  eventId:DS.attr('string'),
	  reponseYes: DS.attr('number'),
	  reponseNo: DS.attr('number'),
	  reponseMaybe: DS.attr('number'),
	  totalAttendee: DS.attr('number'),
	  isSaving : DS.attr('boolean'),
	  files: DS.attr( {
		    defaultValue() { return []; }
		  }),
	recipients: DS.attr( {
	    defaultValue() { return []; }
	  }),
	reactions: DS.attr( {
	    defaultValue() { return []; }
	  }),
	comments: DS.attr( {
	    defaultValue() { return []; }
	  }),
	numberOfComments: DS.attr('number'),
	numberOfReactions: DS.attr('number'),

	tags: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  newTag: "",
	  isValid: Ember.computed('comment', 'isSaving', function() {
		  if(this.get('isSaving')){
			  return false;
		  }
		    return !( typeof this.get('comment') == 'undefined' || this.get('comment').length < 3 );
	  }),
});
