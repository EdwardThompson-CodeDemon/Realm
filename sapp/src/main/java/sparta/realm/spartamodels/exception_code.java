package sparta.realm.spartamodels;


import sparta.realm.spartautils.svars;

/**
 * Created by Thompsons on 1/8/2018.
 */

public class exception_code extends db_class {





    public dynamic_property code=new dynamic_property("code","code",null,false);


    public exception_code()
    {
        super("exception_codes");
//        sync_service_description sd=new sync_service_description();
//        sd.service_name= "Operation data";
//
//        sd.download_link= svars.Global_data_download_link;
//        sd.servic_type= sync_service_description.service_type.Configuration;
//
//        sd.use_download_filter= false;
//        sd.table_name= table_name;
//        ssds=new sync_service_description[]{sd};

    }
    public exception_code( String code)
    {
        super("exception_codes");

           this.code.value=code;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

//        Check if o is an instance of Complex or not
//          "null instanceof [type]" also returns false

        if (!(o instanceof exception_code)) {
            return false;
        }



        // Compare the data members and return accordingly
        return sid.value.equalsIgnoreCase(((exception_code)o).sid.value);
    }

}
