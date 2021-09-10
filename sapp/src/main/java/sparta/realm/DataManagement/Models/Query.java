package sparta.realm.DataManagement.Models;

public class Query {
        public String[] columns,table_filters,order_filters;
        public boolean order_asc;
        public int limit, offset;
        public Query SetColumns(String[] columns){
            this.columns=columns;
            return this;
        }
   public Query SetTableFilters(String[] table_filters){
            this.table_filters=table_filters;
            return this;
        }
   public Query SetOrderFilters(String[] order_columns,boolean order_asc){
            this.order_filters=order_filters;
       this.order_asc=order_asc;
       return this;
        }
 public Query SetOrderAscending(boolean order_asc){
            this.order_asc=order_asc;
            return this;
        }
 public Query SetLimit(int limit){
            this.limit=limit;
            return this;
        }
 public Query SetOffset(int offset){
            this.offset=offset;
            return this;
        }
}
