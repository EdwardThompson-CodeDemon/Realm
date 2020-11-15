package sparta.realm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface DbSync {
    String table_name() default "Igor Brishkoski";
    String id() default "Igor Brishkoski";
    String sid() default "Igor Brishkoski";
//    String reg_time() default "Igor Brishkoski";
//    String id() default "Igor Brishkoski";
//    String id() default "Igor Brishkoski";
//    String id() default "Igor Brishkoski";
//    boolean index_column() default false;
//
//    public String table_name;
//    public dynamic_property id=new dynamic_property(null,"id",null,false);
//    public dynamic_property sid=new dynamic_property("id","sid",null,false);
//    public dynamic_property reg_time=new dynamic_property(null,"reg_time",null,false);
//    public dynamic_property sync_status=new dynamic_property(null,"sync_status",null,false);
//    public dynamic_property data_status=new dynamic_property("is_active","data_status",null,false);
//    public dynamic_property transaction_no=new dynamic_property("transaction_no","transaction_no",null,false);
//    public dynamic_property sync_var=new dynamic_property("datecomparer","sync_var",null,false);
//
//    public dynamic_property user_id=new dynamic_property("user_id","user_id",null,false);
//    public dynamic_property data_usage_frequency=new dynamic_property(null,"data_usage_frequency",null,false);

}
