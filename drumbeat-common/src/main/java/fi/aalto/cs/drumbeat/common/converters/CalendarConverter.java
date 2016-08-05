package fi.aalto.cs.drumbeat.common.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Calendar Converter utils
 * 
 * @author Nam Vu
 *
 */
public class CalendarConverter {
	
	/**
	 * GMT time zone.
	 */
	public static final TimeZone TIME_ZONE_GMT = TimeZone.getTimeZone("GMT"); 
	
	/**
	 * Date format for XSD time values ("yyyy-MM-dd'T'HH:mm:ss") with GMT time zone. 
	 */
	public static final DateFormat XSD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	static {
		XSD_DATE_FORMAT.setTimeZone(TIME_ZONE_GMT);
	}
	
	/**
	 * Converts the XSD time value to {@link Date}.
	 * 
	 * @param xsdDateTime the time value
	 *  
	 * @return the time value with class {@link Date}
	 * 
	 * @throws ParseException
	 */
	public static Date xsdDateTimeToDate(String xsdDateTime) throws ParseException {
		return XSD_DATE_FORMAT.parse(xsdDateTime);
	}
	
	/**
	 * Converts the XSD time value to {@link Calendar}.
	 * 
	 * @param xsdDateTime the time value
	 *  
	 * @return the time value with class {@link Calendar}
	 * 
	 * @throws ParseException
	 */
	public static Calendar xsdDateTimeToCalendar(String xsdDateTime) throws ParseException {
		Calendar calendar = Calendar.getInstance(TIME_ZONE_GMT);
		calendar.setTime(xsdDateTimeToDate(xsdDateTime));
		return calendar;
	}
}
