package sparta.realm.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sparta.realm.spartautils.svars;

public class Conversions {

    public static SimpleDateFormat sdfUserDisplayDate = new SimpleDateFormat("dd-MMM-yyyy");//=null;
    public static SimpleDateFormat sdfUserDisplayTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm");//=null;
    public static SimpleDateFormat sdfUserDisplayTimeOnly = new SimpleDateFormat("HH:mm");//=null;
    public static SimpleDateFormat sdf_user_friendly_date = new SimpleDateFormat("dd-MM-yyyy");//=null;
    public static SimpleDateFormat sdf_db_date = new SimpleDateFormat("yyyy-MM-dd");//=null;
    public static SimpleDateFormat sdf_db_date_unseparated = new SimpleDateFormat("yyyyMMdd");//=null;
    public static SimpleDateFormat sdf_user_friendly_time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//=null;
    public static SimpleDateFormat sdf_db_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//=null;

    public static String getUserDisplayDateFromDBTime(String db_date) {


        try {
            Date time1 = sdf_db_time.parse(db_date);

            return sdfUserDisplayDate.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayDateFromUserTime(String db_date) {


        try {
            Date time1 = sdf_user_friendly_time.parse(db_date);

            return sdfUserDisplayDate.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayTimeFromDBTime(String db_date) {


        try {
            Date time1 = sdf_db_time.parse(db_date);

            return sdfUserDisplayTime.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayTimeOnlyFromDBTime(String db_date) {


        try {
            Date time1 = sdf_db_time.parse(db_date);

            return sdfUserDisplayTimeOnly.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayTimeOnlyFromUserTime(String db_date) {


        try {
            Date time1 = sdf_user_friendly_time.parse(db_date);

            return sdfUserDisplayTimeOnly.format(time1);
        } catch (Exception ex) {
            return db_date;
        }

    }

    public static String getUserDisplayDateFromUserDate(String user_date) {


        try {
            Date time1 = sdf_user_friendly_date.parse(user_date);

            return sdfUserDisplayDate.format(time1);
        } catch (Exception ex) {
            return user_date;
        }

    }


    public static String getUserDisplayTime() {


        try {
            return sdfUserDisplayTime.format(Calendar.getInstance().getTime());
        } catch (Exception ex) {
            return Calendar.getInstance().getTime() + "";
        }

    }

    public static String getUserDisplayDistanceMeter(String distanceInMeters) {
        Double meters = Double.parseDouble(distanceInMeters);
        if (meters > 1000) {
            return Math.round(meters / 1000) + " km";

        } else {
            return Math.round(meters) + " m";

        }

    }

    public static String getUserDisplayDistanceMeterSQ(String distanceInMetersSQ) {
        try {
            Double meters = Double.parseDouble(distanceInMetersSQ);
            if (meters > 1000000) {
                return Math.round(meters / 1000000) + " km²";

            } else {
                return Math.round(meters) + " m²";

            }
        } catch (Exception ex) {
        }
        return distanceInMetersSQ;
    }

}
