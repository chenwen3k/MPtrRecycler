package com.m.mptrrecycler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xs on 14-6-10.
 */
public class CacheTimeUtil {
    public static final long second = 1000;
    public static final long minute = 60 * second;
    public static final long hour = 60 * minute;
    public static final long day = 24 * hour;
    public static final long SIMPLE_MDD_CACHE_TIME = 10 * day;
    public static boolean isSimpleMddCacheOutOfDate(String time){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date createDate = null;
        try{
            createDate = df.parse(time);
        }catch (ParseException e){
            e.printStackTrace();

        }
        if(createDate == null){
            return true;
        }
        Date today = new Date();
        if(today.getTime() - createDate.getTime() > SIMPLE_MDD_CACHE_TIME){
            return true;
        }
        return false;
    }
}
