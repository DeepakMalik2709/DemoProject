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
	  isSubmitted : DS.attr('boolean'),
	  canSubmit : DS.attr('boolean'),
	  title: DS.attr('string'),
	  deadlineTime: DS.attr('number'),
	  isTask : DS.attr('boolean'),
	  isPost : DS.attr('boolean'),
	  isSchedule: DS.attr('boolean'),
	  /* schedule variables */
	  location:DS.attr(""),
		start:DS.attr('number'),
		end:DS.attr('number'),
		eventType:DS.attr('string'),
		backgroundColor:DS.attr('string'),
		borderColor:DS.attr('string'),
		  isSubmitted : DS.attr('boolean'),
		  weekdays:DS.attr( {
		    defaultValue() { return []; }
		  }),
		  fromDate: DS.attr('number'),
		  toDate: DS.attr('number'),
		  allDayEvent : DS.attr('boolean'),
		  /* schedule variables */
		  
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
	  groupIds :DS.attr( {
		    defaultValue() { return []; }
	  }),
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
	 showSubmissionDiv : Ember.computed('isSubmitted', 'canSubmit', function() {
		  if(this.get('canSubmit') && !this.get('isSubmitted')){
			  return true;
		  }
		    return false;
	  }),
});
