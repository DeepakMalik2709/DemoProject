import DS from 'ember-data';

export default DS.Model.extend({
	name: DS.attr('string'),
	  description: DS.attr('string'),
	  isValid: Ember.computed.notEmpty('name'),
	  isSaving : DS.attr('boolean'),
});
