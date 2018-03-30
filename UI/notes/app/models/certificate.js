import DS from 'ember-data';

export default DS.Model.extend({
  certificateId: DS.attr('number'),
  name: DS.attr('string'),
  date: DS.attr('string'),
  image: DS.attr('string'),
  organisation: DS.attr('string'),
  grade: DS.attr('string'),
  appUserId: DS.attr('number'),

  user: DS.belongsTo('user'),

  isEmptyName: Ember.computed.empty('name')
});
