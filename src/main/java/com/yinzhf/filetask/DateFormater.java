package com.yinzhf.filetask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormater {
	public static final String PATTERN_yMdHms = "yyyy-MM-dd HH:mm:ss";
	public static final String PATTERN_yMdHms2 = "yyyy/MM/dd HH:mm:ss";
	public static final String PATTERN_yMdHm = "yyyy-MM-dd HH:mm";
	public static final String PATTERN_yMdHm2 = "yyyy/MM/dd HH:mm";
	public static final String PATTERN_string = "yyyyMMddHHmmss";
	public static final String PATTERN_ISODATE = "yyyy-MM-dd";
	public static final String PATTERN_ISODATE2 = "yyyy/MM/dd";
	public static final String PATTERN_MONTH = "yyyy-MM";
	public static final String PATTERN_MONTH2 = "yyyy/MM";
	public static final String PATTERN_yM = "yyyyMM";
	public static final String PATTERN_yMd = "yyyyMMdd";

	public static FastDateFormat MONTH = FastDateFormat.getInstance(PATTERN_MONTH);
	public static FastDateFormat MONTH2 = FastDateFormat.getInstance(PATTERN_MONTH2);
	public static FastDateFormat DATETIME_yM = FastDateFormat.getInstance(PATTERN_yM);
	public static FastDateFormat DATETIME_yMdHms = FastDateFormat.getInstance(PATTERN_yMdHms);
	public static FastDateFormat DATETIME_yMdHms2 = FastDateFormat.getInstance(PATTERN_yMdHms2);
	public static FastDateFormat DATETIME_yMdHm = FastDateFormat.getInstance(PATTERN_yMdHm);
	public static FastDateFormat DATETIME_yMdHm2 = FastDateFormat.getInstance(PATTERN_yMdHm2);
	public static FastDateFormat DATETIME_string = FastDateFormat.getInstance(PATTERN_string);
	public static FastDateFormat ISODATE = FastDateFormat.getInstance(PATTERN_ISODATE);
	public static FastDateFormat ISODATE2 = FastDateFormat.getInstance(PATTERN_ISODATE2);

	/**
	 * yyyy-MM
	 */
	public static String formatMonth(Date date) {
		if(date == null) {
			return "";
		}
		return MONTH.format(date);
	}

	/**
	 * yyyyMM
	 */
	public static String formatYM(Date date) {
		if(date == null) {
			return "";
		}
		return DATETIME_yM.format(date);
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDatetime(Date date) {
		if(date == null) {
			return "";
		}
		return DATETIME_yMdHms.format(date);
	}

	/**
	 * yyyyMMddHHmmss
	 */
	public static String formatDatetime_SHORT(Date date) {
		if(date == null) {
			return "";
		}
		return DATETIME_string.format(date);
	}

	/**
	 * yyyy-MM-dd
	 */
	public static String formatDate(Date date) {
		if(date == null) {
			return "";
		}
		return DateFormatUtils.ISO_DATE_FORMAT.format(date);
	}

	public static String formatDate(Date date, String format) {
		if(date == null) {
			return "";
		}
		FastDateFormat df = FastDateFormat.getInstance(format);
		return df.format(date);
	}

	/**
	 * yyyy-MM-dd
	 */
	public static Date praseISODate(String format) {
		try {
			return ISODATE.parse(format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static FastDateFormat getDateFormat(String pattern) {
		return FastDateFormat.getInstance(pattern);
	}

	public static Date praseDate(String date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 支持日期格式：
	 * yyyy-MM-dd
	 * yyyy/MM/dd
	 * yyyy-MM-dd HH:mm:ss
	 * yyyy/MM/dd HH:mm:ss
	 * yyyy-MM-dd HH:mm
	 * yyyy/MM/dd HH:mm
	 * yyyyMMddHHmmss
	 * yyyy-MM
	 * yyyyMM
	 * @param date
	 * @return
	 */
	public static Date praseDate(String date) {
		if(StringUtils.isEmpty(date)) {
			return null;
		}
		date = date.trim();
		try {
			if(date.length() <= PATTERN_ISODATE.length() &&
					date.length() >= (PATTERN_ISODATE.length()-2) &&
					countChar(date, '-') == 2) {
				return ISODATE.parse(date);
			}
			else if(date.length() <= PATTERN_ISODATE2.length() &&
					date.length() >= (PATTERN_ISODATE2.length()-2) &&
					countChar(date, '/') == 2) {
				return ISODATE2.parse(date);
			}
			else if(date.length() <= PATTERN_yMdHms.length() &&
					date.length() >= (PATTERN_yMdHms.length()-5) &&
					countChar(date, '-') == 2 &&
					countChar(date, ':') == 2) {
				return DATETIME_yMdHms.parse(date);
			} else if(date.length() <= PATTERN_yMdHms2.length() &&
					date.length() >= (PATTERN_yMdHms2.length()-5) &&
					countChar(date, '/') == 2 &&
					countChar(date, ':') == 2) {
				return DATETIME_yMdHms2.parse(date);
			}
			else if(date.length() <= PATTERN_yMdHm.length() &&
					date.length() >= (PATTERN_yMdHm.length()-4) &&
					countChar(date, '-') == 2 &&
					countChar(date, ':') == 1) {
				return DATETIME_yMdHm.parse(date);
			}
			else if(date.length() <= PATTERN_yMdHm2.length() &&
					date.length() >= (PATTERN_yMdHm2.length()-4) &&
					countChar(date, '/') == 2 &&
					countChar(date, ':') == 1) {
				return DATETIME_yMdHm2.parse(date);
			}
			else if(date.length() == PATTERN_string.length()) {
				return DATETIME_string.parse(date);
			}
			else if(date.length() <= PATTERN_MONTH.length() &&
					date.length() >= (PATTERN_MONTH.length()-1) &&
					countChar(date, '-') == 1) {
				return MONTH.parse(date);
			}
			else if(date.length() <= PATTERN_MONTH2.length() &&
					date.length() >= (PATTERN_MONTH2.length()-1) &&
					countChar(date, '/') == 1) {
				return MONTH2.parse(date);
			}
			else if(date.length() == PATTERN_yM.length()) {
				return DATETIME_yM.parse(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date praseMinDate(String date, String pattern) {
		Date mydate = praseDate(date, pattern);
		Calendar cal = Calendar.getInstance();
		cal.setTime(mydate);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		if(date.length() <= 6) {
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		}
		return cal.getTime();
	}

	public static Date praseMaxDate(String date, String pattern) {
		Date mydate = praseDate(date, pattern);
		Calendar cal = Calendar.getInstance();
		cal.setTime(mydate);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		if(date.length() <= 6) {
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		return cal.getTime();
	}

	public static String addTime(Date date,String end){

		String endTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();

		String tmp[] = end.split(":");
		long hourTmp = Long.parseLong(tmp[0]);
		long miTmp= Long.parseLong(tmp[1]);
		long ssTmp= Long.parseLong(tmp[2]);

		if(hourTmp != 0){
			hourTmp = Long.parseLong(tmp[0])*60*60*1000;
		}
		if(miTmp != 0){
			miTmp = Long.parseLong(tmp[1])*60*1000;
		}
		if(ssTmp != 0){
			ssTmp = Long.parseLong(tmp[2])*1000;
		}

		String temp = sdf.format(date);

		try {

			long currentTime = sdf.parse(temp).getTime();
			long add = hourTmp+miTmp+ssTmp;
			currentTime += add;
			calendar.setTimeInMillis(currentTime);
			endTime = sdf.format(calendar.getTime());

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return endTime;
	}

	public static FastDateFormat getDateFormat(String value,
			String customizeType) {
		FastDateFormat sdf;
		if (StringUtils.isBlank(customizeType)) {
			int length = value.length();
			if (length > 16) {
				sdf = DATETIME_yMdHms;
			} else if (length > 10) {
				sdf = DATETIME_yMdHm;
			} else {
				sdf = ISODATE;
			}
		} else {
			sdf = FastDateFormat.getInstance(customizeType);
		}
		return sdf;
	}

	public static String format(Object value, String format) {
		FastDateFormat sdf;
		if (StringUtils.isBlank(format)) {
			sdf = DATETIME_yMdHms;
		} else {
			sdf = FastDateFormat.getInstance(format);
		}
		if(value instanceof Date) {
			return sdf.format(value);
		} else if(value instanceof java.sql.Date) {
			java.sql.Date sqld = (java.sql.Date)value;
			Date date = new Date(sqld.getTime());
			return sdf.format(date);
		} else if(value instanceof java.sql.Timestamp) {
			java.sql.Timestamp time = (java.sql.Timestamp)value;
			Date date = new Date(time.getTime());
			return sdf.format(date);
		}
		return value.toString();
	}

	private static int countChar(String data, char c) {
		int count = 0;
		if(data != null) {
			for(int i = 0; i < data.length(); ++i) {
				if(data.charAt(i) == c) {
					count++;
				}
			}
		}
		return count;
	}
}
