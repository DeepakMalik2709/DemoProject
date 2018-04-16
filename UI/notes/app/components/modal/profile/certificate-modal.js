export default Ember.Component.extend({

    store: Ember.inject.service(),

    data: null,

    resetOnInit: Ember.on('init', function() {
        this.resetFromCertificate();
    }),

    didInsertElement() {
        var _this = this;
        let certificateId = this.get('item') == undefined ? 0 : this.get('item').get('certificateId');

        $("#certificate-"+certificateId).on("hidden.bs.modal", function() {
            _this.resetFromCertificate();
        });
    },
    
    resetFromCertificate() {
        var store = this.get('store');

        var certificateInfo = store.createRecord('certificate', {
            certificateId: this.get('item') == undefined ? 0 : this.get('item').get('certificateId'),
            name: this.get('item') == undefined ? '' : this.get('item').get('name'),
            date: this.get('item') == undefined ? '' : this.get('item').get('date'),
            image: this.get('item') == undefined ? '' : this.get('item').get('image'),
            organisation: this.get('item') == undefined ? '' : this.get('item').get('organisation'),
            grade: this.get('item') == undefined ? '' : this.get('item').get('grade')
        });

        this.set('data', certificateInfo);
    },

    actions: {
        update(model) {
            this.sendAction('update', model);
        }
    }
});
