package sparta.realm.spartamodels;

import android.util.Log;

import java.util.ArrayList;

public class sdb_model {

    public String db_path,db_name,db_password;
    public ArrayList<sdb_table> tables= new ArrayList<sdb_table>();

    public sdb_model()
    {

    }
    public sdb_model(String db_path, String db_name, String db_password)
    {
        this.db_path=db_path;
        this.db_name=db_name;
        this.db_password=db_password;
    }
    public static class sdb_table
    {
        public String table_name;
      public ArrayList<column> columns =new ArrayList<>();
public sdb_table(String table_name)
{
    this.table_name=table_name;

}




        public String create_statement()
{
    String create_sttm="CREATE TABLE "+table_name;
if(columns.size()>0)
{
    create_sttm+=" (";
for(column cm:columns)
{
    create_sttm+=cm.column_name+" "+cm.data_type+(cm.extra_params==null||cm.extra_params.length()<1?"":" "+cm.extra_params)+" "+(cm.default_value==null||cm.default_value.length()<1?"":" DEFAULT "+cm.default_value)+" ,";

}
    create_sttm=create_sttm.substring(0,create_sttm.length()-1);
    create_sttm+=" )";

}
    Log.e("SDB =>","Create sttmnt :"+create_sttm);
    return create_sttm;
}
public String create_indexes_statement()
{
    String create_sttm="";


for(column cm:columns)
{
    if(cm.index)
    {
        create_sttm+="CREATE INDEX "+table_name+"_"+cm.column_name+" ON "+table_name+"("+cm.column_name+");";

    }





}
if(create_sttm.endsWith(";")){create_sttm=create_sttm.substring(0,create_sttm.length()-1);}
    Log.e("SDB =>","Create indexes sttmnt :"+create_sttm);
    return create_sttm;
}
        public static class column
        {
public String column_name,data_type="TEXT",default_value="",extra_params="";
            public boolean index;
public column(boolean index, String column_name)
{
    this.column_name=column_name;
    this.index=index;
}



public column(boolean index, String column_name, String data_type)
{
    this.column_name=column_name;
    this.data_type=data_type;
    this.index=index;

}
public column(boolean index, String column_name, String data_type, String default_value)
{
    this.column_name=column_name;
    this.data_type=data_type;
    this.default_value=default_value;
    this.index=index;

}
        }

    }

}
