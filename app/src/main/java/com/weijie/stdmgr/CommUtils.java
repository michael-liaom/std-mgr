package com.weijie.stdmgr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommUtils {
    public static String toLocalDateString(Date date){
        SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dbSdf.format(date);
    }

}
