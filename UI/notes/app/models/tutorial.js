import DS from 'ember-data';

export default DS.Model.extend({
	dbFields : ["id", "title", "description", "url" , "tags", "language", "files", "languagesUI"],
	title: DS.attr('string'),
	  description: DS.attr('string'),
	  url: DS.attr('string'),
	  convertToEmbed :true,
	  tags: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  files: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  languagesUI: DS.attr( {
		    defaultValue() { return []; }
	  }),
	  newTag: "",
	  showVideo : true,
	  isSaving : DS.attr('boolean'),
	  isValid: Ember.computed('title', 'isSaving', function() {
		  if(this.get('isSaving')){
			  return false;
		  }
		  return !( typeof this.get('title') == 'undefined' || this.get('title').length < 3 );
	  }),
	 // createdBy: DS.attr('long'),
	//  createdTime: DS.attr('long'),
	 // lastModifiedTime: DS.attr('long'),
	 //
});
