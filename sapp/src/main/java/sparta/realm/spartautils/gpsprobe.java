package sparta.realm.spartautils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;

/**
 * Created by Thompsons Sparta on 07-Dec-16.
 */
public class gpsprobe {
    Activity act;
    Location currentlocation = null;
    Location previouslocation = null;

    public gpsprobe(Activity act) {
        this.act = act;
        LocationManager locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        final String provider = locationManager.getBestProvider(criteria, false);
        if (act.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && act.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return ;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        previouslocation = location;
        LocationListener LL = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                previouslocation = currentlocation;
                currentlocation = location;

                Log.e("" + location.getLatitude(), ";" + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (act.checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && act.checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LL);


}
    public Location getCurrentlocation()
    {

        return currentlocation;
    }

    public Location getPreviouslocation()
    {

        return currentlocation;
    }





}
