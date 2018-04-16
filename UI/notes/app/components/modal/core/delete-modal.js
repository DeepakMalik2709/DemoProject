export default Ember.Component.extend({

    title: "Delete",
    content: "Do you want to delete?",

    setInit: Ember.on('init', function() {
        if(this.get("title") != undefined) {
            this.title = this.get("title");
        }

        if(this.get("content") != undefined) {
            this.content = this.get("content");
        }
    }),
    
    actions: {
        delete(id) {
            this.sendAction('delete', id);
        }
    }
});
