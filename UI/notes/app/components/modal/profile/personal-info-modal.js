import Ember from 'ember';

export default Ember.Component.extend({

    store: Ember.inject.service(),

    data: null,

    resetOnInit: Ember.on('init', function() {
        this.resetFromProfile();
    }),

    didInsertElement() {
        var _this = this;

        $("#personal-info").on("hidden.bs.modal", function() {
            _this.resetFromProfile();
        });
    },
    
    resetFromProfile() {
        var store = this.get('store');

        var profile = store.createRecord('user', {
            firstName: this.get('item').get('firstName'),
            lastName: this.get('item').get('lastName'),
            emailId: this.get('item').get('emailId'),
            currentCity: this.get('item').get('currentCity'),
            phoneNumber: this.get('item').get('phoneNumber'),
            about: this.get('item').get('about')
        });

        this.set('data', profile);
    },

    actions: {
        update(model) {
            this.sendAction('update', model);
        }
    }
});
