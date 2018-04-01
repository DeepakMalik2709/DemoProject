export default Ember.Component.extend({

    saveBtnName: "Save Changes",

    setInit: Ember.on('init', function() {
        if(this.get("saveBtnName") != undefined) {
            this.saveBtnName = this.get("saveBtnName");
        }
    }),

    actions: {
        save(model) {
            this.sendAction('save', model);
        }
    }
});
