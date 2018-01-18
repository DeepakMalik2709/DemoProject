import DS from 'ember-data';

export default DS.Model.extend({
	
	name: DS.attr('string'),
	email: DS.attr('string'),
	joinRequestApprover: DS.attr('string'),
	department: DS.attr('string'),
	organization: DS.attr('string'),
	uniqueId: DS.attr('string'),
	rollNo: DS.attr('string'),
	joinRequestApproveDate:DS.attr('date',{defaultValue() { return new Date(); }}),
	isAppUser: DS.attr('boolean'),
	isBlocked: DS.attr('boolean'),
	isJoinRequestApproved: DS.attr('boolean'),
	isNotificationSent : DS.attr('boolean'),
	institute: DS.attr( ),
});
