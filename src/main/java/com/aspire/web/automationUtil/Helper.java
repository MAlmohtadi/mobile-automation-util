package com.aspire.web.automationUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;


public class Helper {
	public static String DAY_MONTH_YEAR = "dd/MM/yyyy";
	public static String MONTH_DAY_YEAR = "MM/dd/yyyy";
	public static String YEAR_MONTH_DAY = "yyyy/MM/dd";

	public static String getDateInThisMonth(String format) {
		Calendar startingDateCalendar = Calendar.getInstance();
		int day = startingDateCalendar.get(Calendar.DAY_OF_MONTH);
		int minDaysToGoBack = (day > 7) ? 7 : 1;
		int maxDaysToGoBack = day - 1;
		int randomNum = randInt(minDaysToGoBack, maxDaysToGoBack);
		startingDateCalendar.add(Calendar.DAY_OF_WEEK, -randomNum);
		Date randomDate = startingDateCalendar.getTime();
		String randomDateWithFormat = getDateFormat(format).format(randomDate);
		return randomDateWithFormat;
	}

	public static String getDateInThisQuarter(Date startingDate, String format) {
		Calendar startingDateCalendar = new GregorianCalendar();
		startingDateCalendar.setTime(startingDate);
		int day = startingDateCalendar.get(Calendar.DAY_OF_MONTH);
		int thisMonth = startingDateCalendar.get(Calendar.MONTH) + 1;
		int monthNumberInQuarter = (thisMonth % 3) == 0 ? 3 : thisMonth % 3;
		int numberSelectablePastMonths = monthNumberInQuarter - 1; // we want to
																	// avoid
																	// selecting
																	// current
																	// month
		int minDaysToGoBack = (numberSelectablePastMonths == 0) ? (day > 7) ? 7
				: 0 : day;
		int maxDaysToGoBack = (numberSelectablePastMonths == 0) ? day - 1 : day
				+ numberSelectablePastMonths * 30;
		int randomDaysToGoBackInQuarter = randInt(minDaysToGoBack,
				maxDaysToGoBack);
		Calendar quarterCalendar = new GregorianCalendar();
		quarterCalendar.add(Calendar.DAY_OF_YEAR, -randomDaysToGoBackInQuarter);
		Date randomDate = quarterCalendar.getTime();
		String randomDateWithFormat = getDateFormat(format).format(randomDate);
		return randomDateWithFormat;
	}

	public static String getDateInThisWeek(String format) {
		Calendar cal = Calendar.getInstance();
		int today = cal.get(Calendar.DAY_OF_WEEK);
		Date date = new Date();
		if (today == Calendar.SATURDAY) {
			return getDateFormat(format).format(date);
		} else {
			int randomNum = randInt(1, today);
			cal.add(Calendar.DAY_OF_WEEK, -randomNum);
			Date randomDate = cal.getTime();
			String randomDateWithFormat = getDateFormat(format).format(
					randomDate);
			return randomDateWithFormat;
		}
	}

	public static String getRandomDateWithinTwoWeeksRange(String format) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_MONTH, 2);
		int randomNum = randInt(1, 14);
		cal.add(Calendar.DAY_OF_WEEK, -randomNum);
		Date randomDate = cal.getTime();
		String randomDateWithFormat = getDateFormat(format).format(randomDate);
		return randomDateWithFormat;
	}

	public static String getRandomDateFromNextWeekDiscardTodayDate(String format) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_MONTH, 2);
		int randomNum = randInt(1, 7);
		cal.add(Calendar.DAY_OF_WEEK, -randomNum);
		Date randomDate = cal.getTime();
		String randomDateWithFormat = getDateFormat(format).format(randomDate);
		return randomDateWithFormat;
	}

	public static String getDateInThePastWithinOneWeekRange(String format) {
		Calendar cal = Calendar.getInstance();
		int randomNum = randInt(1, 7);
		cal.add(Calendar.DAY_OF_WEEK, -randomNum);
		Date randomDateInPast = cal.getTime();
		String dateWithFormat = getDateFormat(format).format(randomDateInPast);
		return dateWithFormat;
	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive. The
	 * difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value. Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	/**
	 * @param numberOfDays
	 *            get number of days to move after , before or stay in the
	 *            current day from story
	 * @param format
	 *            get the date format from class to set the date type
	 * @return the past,future or stay in current date
	 */
	public static String getDateInThePastOrFuture(int numberOfDays,
			String format) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, numberOfDays);
		Date pastOrFeautureDate = cal.getTime();
		String dateInThePastOrFuture = getDateFormat(format).format(
				pastOrFeautureDate);
		return dateInThePastOrFuture;

	}

	public static DateFormat getDateFormat(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);

		return dateFormat;

	}

	/**
	 * This method convertMonthToInt aims to convert string month to integer.
	 * 
	 * @param month
	 *            get month in string to be converted.
	 * @return month in integer value.
	 * @throws ParseException
	 */
	public static int convertMonthToInt(String month) throws ParseException {

		Date date = new SimpleDateFormat("MMMM").parse(month);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int Month = cal.get(Calendar.MONTH) + 1;// note:cal.get(Calendar.MONTH)
												// get previous month
		// so we add to it 1 to get current month.
		return Month;
	}

	/**
	 * @param date
	 *            the date we want to send it, if you want today as example you
	 *            can send it as Date date = new Date();
	 * @param format
	 *            the format of date. ex:dd/MM/yyyy
	 * @return the formatted date as a string.
	 */
	public static String getDateAsFormattedString(Date date, String format) {
		String now = getDateFormat(format).format(date);
		return now;
	}
	
	
	
	
	public String generateUsername(int size) {

		return new String(RandomStringUtils.randomAlphabetic(6));
	}

	public String generatePassword(int sizeAlphabetic,int sizeNumeric) {

		return new String(RandomStringUtils.randomAlphabetic(sizeAlphabetic) + RandomStringUtils.randomNumeric(sizeNumeric));

	}

	public String generateEmail(int size, String type) {
		return new String(RandomStringUtils.randomAlphabetic(size)+"@"+type+".com");

	}
}