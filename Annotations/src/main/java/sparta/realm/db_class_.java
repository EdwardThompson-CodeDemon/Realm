package sparta.realm;

import java.io.Serializable;

/**
 * Created by Thompson on 03-Mar-17.
 */

public class db_class_ implements Cloneable, Serializable {


 //   @DynamicProperty(column_name = "id", json_key ="tablet_data_position", column_data_type = "INTEGER",extra_params = "PRIMARY KEY AUTOINCREMENT")
    @DynamicProperty(column_name = "_id", json_key ="tablet_data_position", column_data_type = "INTEGER",extra_params = "PRIMARY KEY AUTOINCREMENT")
    public String id="";

    @DynamicProperty(json_key = "id", column_name = "sid", indexed_column = true)
    public String sid="";

    @DynamicProperty(column_name = "reg_time", column_data_type = "DATETIME", column_default_value = "(datetime('now','localtime'))")
    public String reg_time="";

    @DynamicProperty(column_name = "sync_status")
    public String sync_status="";

    @DynamicProperty(json_key = "is_active", column_name = "data_status")
    public String data_status="";

    @DynamicProperty(json_key = "unique_code", column_name = "transaction_no")
    public String transaction_no="";

    @DynamicProperty(json_key = "datecomparer", column_name = "sync_var")
    public String sync_var="";

    @DynamicProperty(json_key = "user_id", column_name = "user_id")
    public String user_id="";

    @DynamicProperty(column_name = "data_usage_frequency")
    public String data_usage_frequency="";





    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    }
