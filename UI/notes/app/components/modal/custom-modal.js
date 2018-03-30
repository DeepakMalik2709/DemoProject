export default Ember.Component.extend({

    actions: {
        save(model) {
            this.sendAction('save', model);
        }
    }
});
