package com.m.mptrrecycler.util;

import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
	public static final String yyyy_MM_dd_hh_mm = "yyyy-MM-dd hh:mm";
	public static final String yyyy_MM_dd = "yyyy-MM-dd";
	public static final String yyyy_MM_dd_hh_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyy_DOT_MM_DOT_dd = "yyyy.MM.dd";

	public static String getDateTime(long timeSeconds) {
		return DateFormat.format("MM.dd hh:mm:ss", timeSeconds * 1000).toString();
	}
	
	public static String getDate(long timeSeconds) {
		return getDate(timeSeconds, "yyyy.MM.dd").toString();
	}

    public static String getDate(long timeSeconds, String format) {
        return DateFormat.format(format, timeSeconds * 1000).toString();
    }
    public static String getDateInMillis(long timeSeconds, String format) {
        return DateFormat.format(format, timeSeconds).toString();
    }

    public static String formatDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String formatDateUs(Date date){

        Calendar cal= Calendar.getInstance();
        Locale locale = Locale.US;
        SimpleDateFormat format = new SimpleDateFormat("MMM, dd", locale);
        return format.format(date);
    }

    public static String formatDate(Date date, String patten){
        if(date != null && !TextUtils.isEmpty(patten)){
            SimpleDateFormat format = new SimpleDateFormat(patten);
            return format.format(date);
        }
        return null;
    }
    public static String formatDateNoYear(Date date){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        return format.format(date);
    }

    public static Date parseDate(String string, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        Date date = new Date();
        try{
            date = format.parse(string);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    public static String getRefreshTimeText(long refreshTime){
        if(refreshTime == 0){
            return "";
        }
        String tips;
        long now = System.currentTimeMillis();
        long offset = now - refreshTime;
        Date nowDate = new Date(now);
        Date reDate = new Date(refreshTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        int y = calendar.get(Calendar.YEAR);
        calendar.setTime(reDate);
        int y1 = calendar.get(Calendar.YEAR);
        if(offset < CacheTimeUtil.minute){
            tips = "刚刚";
        }else if(offset >= CacheTimeUtil.minute && offset < CacheTimeUtil.hour){
            tips = (offset / CacheTimeUtil.minute) + "分钟以前";
        }else if(offset >= CacheTimeUtil.hour && offset < CacheTimeUtil.day){
            tips = (offset / CacheTimeUtil.hour) + "小时以前";
        }else if(offset >= CacheTimeUtil.day && offset < CacheTimeUtil.day * 3){
            tips = (offset / CacheTimeUtil.day) + "天以前";
        }else if(offset >= CacheTimeUtil.day * 3 && y == y1){
            tips = DateTimeUtils.getDateInMillis(refreshTime, "MM月dd日");
        }else{
            tips = DateTimeUtils.getDateInMillis(refreshTime, "yyyy年MM月dd日");
        }
        return tips;
    }

    public static boolean isOutOfDate(String start, String end){
        boolean out = true;
        Date today = new Date();
        Date startDate = DateTimeUtils.parseDate(start, yyyy_MM_dd_hh_mm);
        Date endDate = DateTimeUtils.parseDate(end, yyyy_MM_dd_hh_mm);
        if(today.after(startDate) && today.before(endDate)){
            out = false;
        }
        return out;
    }

}
