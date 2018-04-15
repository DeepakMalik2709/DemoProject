import Ember from 'ember';
import Table from 'ember-light-table';

export default Ember.Component.extend({

	columns:Ember.computed(function() {
    return this.tableSchema;
  }),
	dateFormat:function(value){ var date =new Date(value); return date.getDay()+"/"+date.getMonth();}   ,
  init() {
	    this._super(...arguments);
	    this.refreshTable( this.rows);

  },
  refreshTable(data){
	  let table = new Table(this.get('columns'),data, { enableSync: true });
      let sortColumn = table.get('allColumns').findBy('valuePath', this.get('sort'));

      // Setup initial sort column
      if (sortColumn) {
      sortColumn.set('sorted', true);
      }

      this.set('table', table);
  },
	format(item){
		return new Date(item);
	},
    actions: {

				rowAction(row){
					this.sendAction('rowClickAction',row, event);
						console.log(row);
				},
    	 onColumnClick(column) {
    	      if (column.sorted) {
    	        this.setProperties({
    	          dir: column.ascending ? 'asc' : 'desc',
    	          sort: column.get('valuePath'),
    	          canLoadMore: true,
    	          page: 0
    	        });
    	        this.get('model').clear();
    	      }
    	    },
        willTransition(transition) {
         //   let model = this.controller.get('model');
            /*
             * if(model.get('hasDirtyAttributes')){ let confirmation =
             * confirm("leave without saving ? "); if(confirmation){
             * model.rollbackAttributes(); }else{ transition.abort(); } }
             */
        }
    }

});
