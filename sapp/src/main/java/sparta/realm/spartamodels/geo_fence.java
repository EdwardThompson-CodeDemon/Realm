package sparta.realm.spartamodels;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class geo_fence {


    /*

    sdb_model.sdb_table geo_fences_table=new sdb_model.sdb_table("geo_fences_table");

        geo_fences_table.columns.addAll(common_columns);
        geo_fences_table.columns.add(new sdb_model.sdb_table.column("fence_type"));
        geo_fences_table.columns.add(new sdb_model.sdb_table.column("parent_type_id"));
        geo_fences_table.columns.add(new sdb_model.sdb_table.column("parent_id"));
        geo_fences_table.columns.add(new sdb_model.sdb_table.column("activity_status"));
geo_fences_table.columns.add(new sdb_model.sdb_table.column("radius"));
geo_fences_table.columns.add(new sdb_model.sdb_table.column("color"));

sdb_model.sdb_table geo_fence_points_table=new sdb_model.sdb_table("geo_fence_points_table");

        geo_fence_points_table.columns.addAll(common_columns);
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column("fence_id"));
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column("lat"));
        geo_fence_points_table.columns.add(new sdb_model.sdb_table.column("lon"));

     */
    public enum geo_fence_parent_type
    {
        no,
        site,
        device,
        compartment,
        activity,
        individual

    }
    public String lid,sid,fence_type,parent_type_id,parent_id,_points_radius,activity_status,color;
    public ArrayList<geo_fence_points> points = new ArrayList<geo_fence_points>();



    public static class geo_fence_points{
 public LatLng point ;
public String radius;
public geo_fence_points(LatLng point, String radius)
{
    this.point=point;
    this.radius=radius;
}
}
    public geo_fence()
    {

    }





}
