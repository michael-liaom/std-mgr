package com.weijie.stdmgr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by weijie on 2018/5/19.
 */
public class CommUtils {
    public static String toLocalDateString(Date date){
        SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINA);
        return dbSdf.format(date);
    }

    public static String toLocalDatetimeString(Date date){
        SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        return dbSdf.format(date);
    }

    public static Date toTimestamp(String datetime_str){
        SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date datetime;
        try {
            datetime = dbSdf.parse(datetime_str);
        }
        catch (java.text.ParseException e){
            e.printStackTrace();
            datetime = new Date(0);
        }

        return datetime;
    }

    public static Date toDate(String date_str){
        SimpleDateFormat dbSdf
                = new SimpleDateFormat("yyyy.MM.dd",Locale.getDefault());
        Date date = new Date(0);
        try {
            date = dbSdf.parse(date_str);
        }
        catch (java.text.ParseException e){
            e.printStackTrace();
        }

        return date;
    }
}
