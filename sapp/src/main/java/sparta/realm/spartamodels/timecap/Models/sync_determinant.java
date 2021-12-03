package sparta.realm.spartamodels.timecap.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.db_class_;

import java.io.Serializable;

@DynamicClass(table_name = "sync_determinant_table")

public class sync_determinant extends db_class_ implements Serializable {





    @DynamicProperty(column_name = "syncing_images")
    public String syncing_images="";

}
