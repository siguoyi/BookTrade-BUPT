package com.bupt.booktrade.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormat {
	public static String transTime(Long l){
		Date date = new Date(l);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	
}
