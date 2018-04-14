import DS from 'ember-data';

export default DS.Model.extend({
	dbFields : ["id","description","name", "subject" , "marks", "fromDateTime", "toDateTime", "passingRules","totalAppeared","groups","questions"],

	description: DS.attr('string'),
	name: DS.attr('string'),
	subject: DS.attr('string'),
	createdByEmail: DS.attr('string'),
  marks: DS.attr('string'),
  passingRules: DS.attr('string'),
  fromDateTime: DS.attr('number'),
  toDateTime: DS.attr('number'),
  totalAppeared: DS.attr('number'),
	questions:  DS.attr( {
			defaultValue() { return []; }
		}),
  groups:  DS.attr( {
			defaultValue() { return []; }
		})

});
