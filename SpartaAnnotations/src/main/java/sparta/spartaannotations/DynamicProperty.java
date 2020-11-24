package com.realm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DynamicProperty {

    String column_name() default "";
    String json_key() default "";
    boolean indexed_column() default false;
    String column_data_type() default "TEXT";
    String column_default_value() default "";
    String extra_params() default "";
    String storage_mode() default "";
    int ai_error_sample_tolerance() default 100;


}
