package sparta.realm.spartamodels;


import java.io.Serializable;

public class fingerprint_to_capture implements Serializable {
   public String data;//,lid="",task_name,workers_limit,free_slots,occupied_slots,task_operation_mode,working_field;//,pob,phoneno="",email,job_unit,job_category,job_status,region,ministry,sync_var,sid,employee_status,gender_id;
    public int index;
    public boolean capturing;
    public boolean skipped;
 public int drawable_resource;
    public String name;


    public fingerprint_to_capture(int index,int drawable_resource,String name)
    {
this.index=index;
this.drawable_resource=drawable_resource;
this.name=name;
    }

}
