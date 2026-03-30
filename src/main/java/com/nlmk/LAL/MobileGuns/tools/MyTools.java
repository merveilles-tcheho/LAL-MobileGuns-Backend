package com.nlmk.LAL.MobileGuns.tools;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTools {

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private final static SimpleDateFormat TS_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private final static Logger logger = LoggerFactory.getLogger(MyTools.class);

	public static boolean isNullOrEmpty(String value) {
		return value == null || value.trim().equals("");
	}

	public static String getRandomStringFromCurrentTime() {
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		Random random = new Random();

		String randomString = String.valueOf(random.nextInt(1000));

		return now + randomString;
	}

	public static BigDecimal getRandomNumberFromCurrentTime() {
		String now = String.valueOf(System.currentTimeMillis());

		Random random = new Random();

		String randomString = String.valueOf(random.nextInt(1000));

		return new BigDecimal(now + randomString);
	}

	public static String getNowFormattedTimestamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public static Timestamp getDefaultTimestamp() {
		return Timestamp.valueOf(LocalDateTime.of(1, 1, 1, 0, 0, 0));
	}

	public static String convertTimeStampToString(Timestamp ts, String pattern) {
		if (ts.equals(getDefaultTimestamp()))
			return "";

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

		return formatter.format(ts.toLocalDateTime());
	}

	public static void logInfo(String msg) {
		logger.info(msg);
	}

	public static void logError(String msg) {
		logger.error(msg);
	}

	public static void logError(String msg, Exception ex) {
		logger.error(msg, ex);
	}

	public static String addChar(String str, char c, int desiredLength) {
		if (str == null)
			str = "";

		if (str.length() >= desiredLength)
			return str;

		StringBuilder sb = new StringBuilder(str);

		for (int i = str.length(); i < desiredLength; i++)
			sb.append(c);

		return sb.toString();
	}

	public static String trimNull(String text) {
		if (text != null) {
			return text.trim();
		} else {
			return "";
		}
	}

	public static String encodeString(String textToEncode) {
		return Base64.getEncoder().encodeToString(textToEncode.getBytes());
	}

	public static String decodeString(String encodedString) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

		return new String(decodedBytes);
	}

	public static String formatDbDateString(String dateStr) {
		try {
			if (isNullOrEmpty(dateStr))
				return "";

			if (dateStr.equals("0001-01-01"))
				return "";

			DateTimeFormatter f = DateTimeFormatter.ofPattern("uuuu-MM-dd");
			LocalDate ld = LocalDate.parse(dateStr, f);
			DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.ITALY);

			return ld.format(dtf);
		} catch (Exception e) {
			logError("Exception in formatDbDateString " + dateStr, e);

			return dateStr;
		}
	}

	public static String formatDbTimeString(String timeStr) {
		try {
			if (isNullOrEmpty(timeStr))
				return "";

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
			LocalTime localTimeObj = LocalTime.parse(timeStr, formatter);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

			return localTimeObj.format(dtf);
		} catch (Exception e) {

			logError("Exception in formatDbTimeString " + timeStr, e);

			return timeStr;
		}
	}

	public static String formatDate(Date dateToFormat) {
		if (dateToFormat == null)
			return "";

		return DATE_FORMAT.format(dateToFormat);
	}

	public static String formatNow(String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

		return LocalDateTime.now().format(formatter);
	}

	public static String formatTimeStamp(Timestamp tsToFormat) {
		if (tsToFormat == null)
			return "";

		return TS_FORMAT.format(tsToFormat);
	}

	public static Date parseDate(String dateString) throws ParseException {
		if (isNullOrEmpty(dateString))
			return null;

		return DATE_FORMAT.parse(dateString);
	}

	public static Timestamp parseTimestamp(String tsString) {
		DateTimeFormatter usFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
		try {
			LocalDateTime localDateTime = LocalDateTime.from(usFormatter.parse(tsString));

			return Timestamp.valueOf(localDateTime);
		} catch (DateTimeParseException dtpe) {
			DateTimeFormatter itFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

			LocalDateTime localDateTime = LocalDateTime.from(itFormatter.parse(tsString));

			return Timestamp.valueOf(localDateTime);
		}
	}

	public static Boolean getBoolean(String str) {
		if (isNullOrEmpty(str))
			return null;

		return str.trim().equalsIgnoreCase("Y");
	}

	public static Date getNowDateWithoutTime() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}

		try {
			Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	public static String padLeftZeros(String inputString, int length) {
		if (inputString.length() >= length) {
			return inputString;
		}

		StringBuilder sb = new StringBuilder();

		while (sb.length() < length - inputString.length()) {
			sb.append('0');
		}

		sb.append(inputString);

		return sb.toString();
	}

	public static String getFirstChars(String str, int charLength) {
		if (isNullOrEmpty(str))
			return "";

		if (str.length() > charLength)
			return str.substring(0, charLength);
		else
			return str;
	}
}
