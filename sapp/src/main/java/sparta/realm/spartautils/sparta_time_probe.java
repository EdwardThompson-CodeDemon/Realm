package sparta.realm.spartautils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Thompsons on 10/5/2018.
 */

public class sparta_time_probe {

    public static String getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }


    /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("" + (int) dayCount);
    }

    public static long cal_diff(String from_date, String to_date)
    {
        Calendar calendar_min = Calendar.getInstance();
        Calendar calendar_max = Calendar.getInstance();
        try{
            calendar_min.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(from_date));
             calendar_max.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(to_date));
        }catch (Exception ex){}

        long msDiff = calendar_max.getTimeInMillis()-calendar_min.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(msDiff);
    }
    public static int get_working_days(String from_date, String to_date)
    {
        int day_count=0;
        Calendar calendar_max = Calendar.getInstance();
        try{
            calendar_max.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(from_date));
        }catch (Exception ex){}

        for(int i =((int)cal_diff(from_date,to_date));i>0;i--)
        {
            String dayLongName = calendar_max.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            Log.e("Logging day =>",""+dayLongName);
            if(svars.consider_saturday_as_a_working_day_for_leave)
            {
                if(!dayLongName.equalsIgnoreCase("sunday"))
                {
                    day_count++;

                }
            }else{
                if(!dayLongName.equalsIgnoreCase("saturday")&&!dayLongName.equalsIgnoreCase("sunday"))
                {
                    day_count++;

                }
            }
/**/
            calendar_max.add(Calendar.DATE, 1);
        }
        return day_count;
    }


}
