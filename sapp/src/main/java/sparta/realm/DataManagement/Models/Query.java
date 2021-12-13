package sparta.realm.DataManagement.Models;

public class Query {
        public String[] columns,table_filters,order_filters;
        public boolean order_asc;
        public int limit, offset;
        public Query setColumns(String... columns){
            this.columns=columns;
            return this;
        }
   public Query setTableFilters(String... table_filters){
            this.table_filters=table_filters;
            return this;
        }
   public Query setOrderFilters(boolean order_asc,String... order_columns){
            this.order_filters=order_filters;
       this.order_asc=order_asc;
       return this;
        }
 public Query setOrderAscending(boolean order_asc){
            this.order_asc=order_asc;
            return this;
        }
 public Query setLimit(int limit){
            this.limit=limit;
            return this;
        }
 public Query setOffset(int offset){
            this.offset=offset;
            return this;
        }
}
