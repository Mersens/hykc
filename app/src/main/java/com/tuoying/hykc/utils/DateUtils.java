package com.tuoying.hykc.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s, String formatType) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatType);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static long date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date_str).getTime() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static Date strToDate(String strDate,String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date strtodate = null;
        try {
            strtodate = formatter.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strtodate;
    }
    public static String strTimeFormat(String time,String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date=strToDate(time,"yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
    public static String strTimeFormat1(String time,String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date=strToDate(time,"yyyyMMdd");
        return formatter.format(date);
    }
    public static String getStringNowTime(){
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter2.format(new Date());

    }

    public static long formatTime(String time1){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long days=0;
        try
        {
            Date d1 = df.parse(time1);
            Date d2 = new Date();
            long diff = (d2.getTime() - d1.getTime())/1000;
            days = diff/60;
        }
        catch (Exception e)
        {
        }
        return days;

    }

    public static long getTimeMillis(){
        return System.currentTimeMillis();

    }

    public static long getBetwnTime(long l1,long l2){
        return (l1 - l2);
    }
}
