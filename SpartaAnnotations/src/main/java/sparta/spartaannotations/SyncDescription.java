package com.realm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Repeatable(SyncDescriptions.class)
public @interface SyncDescription {
    String service_name() default "Annonymous";
    String[] table_filters() default {};
    String upload_link() default "";
    String download_link() default "";
    service_type service_type() default service_type.Null;
    int chunk_size() default 1000;
    boolean use_download_filter() default true;



     enum service_type{
        Null,
        Upload,
        Download,
        Download_Upload,
        Configuration
    }

}
