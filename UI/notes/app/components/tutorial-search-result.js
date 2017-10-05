import Ember from 'ember';

export default Ember.Component.extend({
    actions: {

        toggleVideo() {
            this.toggleProperty("item.showVideo")
        },
    }
});