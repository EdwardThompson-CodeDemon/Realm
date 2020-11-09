package sparta.realm.spartamodels;


import sparta.realm.spartautils.svars;

/**
 * Created by Thompsons on 1/8/2018.
 */

public class dyna_data_obj extends db_class {



    public enum operatio_data_type{
        Null,
        GenderList,
        ProffessionList,
        ConsulateList,
        ZonesList,
        LocalitiesList,
        CommunesList,
        IdTypesList,
        BloodGroupsList,
        DelegatesList,
        TeintsList

    }
    public dynamic_property name=new dynamic_property("name","name",null,false);
    public dynamic_property data_type=new dynamic_property("data_type","data_type",null,true);
    public dynamic_property parent=new dynamic_property("parent","parent",null,true);
        public dynamic_property data=new dynamic_property("data","data",null,false);
        public dynamic_property data_2=new dynamic_property("data","data_2",null,false);
    public dynamic_property code=new dynamic_property("code","code",null,false);

    public dyna_data_obj()
    {
        super("dyna_data_table");
        sync_service_description sd=new sync_service_description();
        sd.service_name= "Operation data";

        sd.download_link= svars.Global_data_download_link;
        sd.servic_type= sync_service_description.service_type.Configuration;

        sd.use_download_filter= false;
        sd.table_name= table_name;
        ssds=new sync_service_description[]{sd};

    }
    public dyna_data_obj(String lid,String sid, String name, String data_type, String data, String parent, String code)
    {
        super("dyna_data_table");
        this.sid.value=sid;
        this.name.value=name;
        this.data_type.value=data_type;
        this.parent.value=parent;
        id.value=lid;
        this.data.value=data;
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

        if (!(o instanceof dyna_data_obj)) {
            return false;
        }



        // Compare the data members and return accordingly
        return sid.value.equalsIgnoreCase(((dyna_data_obj)o).sid.value);
    }

}
