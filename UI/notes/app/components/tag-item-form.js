import Ember from 'ember';

export default Ember.Component.extend({
    buttonLabel: 'Save',
    actions: {

        saveTag(param) {
           this.sendAction('saveTag', param);
        },

        cancelClicked(param) {
            this.sendAction('cancelClicked', param);
        },
    }
});