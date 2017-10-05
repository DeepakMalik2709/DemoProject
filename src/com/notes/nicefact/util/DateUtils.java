package com.notes.nicefact.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DateUtils {

	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
	public static final String LONG_DATE_PATTERN = "MMMM, yyyy.MM.dd (EEE)";
	public static final String DATE_PATTERN = "yyyy/MM/dd";
	public static final String MONTH_YEAR_PATTERN = "MMMM, yyyy";
	public static final String TIME_PATTERN = "H:mm";
	public static final String DEFAULT_PATTERN = "dd MMM yyyy";
	public static String detailTimePattern = "a hh:mm";;
	
	static SimpleDateFormat formatter ; 
	
	public static String currentFormat ;
	
	public static String formatDate(Date date){
		return formatDate(date, DateUtils.DEFAULT_PATTERN);
	}
	public static String formatDate(Date date, String format){
		String formattedDate = "";
		if(format!=null && date!=null){
			if(!format.equals(currentFormat)){
				currentFormat = format;
				getFormatter().applyPattern(currentFormat);
			}
			formattedDate = getFormatter().format(date);
		}
		return formattedDate;
	}
	
	static SimpleDateFormat getFormatter(){
		if(formatter==null){
			formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
		}
		return formatter;
	}
	
	public static Date addTimeFromStringToDate(String dateWOTime, String timeStr){
		Date date = null;
		if(StringUtils.isNotBlank(timeStr)){
			SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN);
			SimpleDateFormat formatter = new SimpleDateFormat(TIME_PATTERN);
			try {
				date = dateFormatter.parse(dateWOTime);
				Date time = formatter.parse(timeStr);
				Calendar timeCal = Calendar.getInstance();
				timeCal.setTime(time);
				
				Calendar dateCal = Calendar.getInstance();
				dateCal.setTime(date);
				dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
				dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
				date = dateCal.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		
		}
		
		return date;
	}
}
