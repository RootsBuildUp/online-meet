package com.ticonsys.online_meet.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class DateTimeUtil {
    public static final String PATTERN = "dd-MM-yyyy";
    public static final String PATTERN2 = "dd-MM-yyyy hh:mm:ss";

    public static final String DB_PATTERN = "yyMMdd";

    public static Date convertStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_PATTERN);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            log.error("error during parsing date: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN);
        return simpleDateFormat.format(date);
    }

    public static String convertStringDateToDbStringFormat(String stringDate) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(PATTERN);

        try {
            Date date = inputDateFormat.parse(stringDate);

            if (isValidDate(date, stringDate)) {
                SimpleDateFormat outputDateFormat = new SimpleDateFormat(DB_PATTERN);
                String outputDateStr = outputDateFormat.format(date);

                log.info("Converted Date: {}", outputDateStr);
                return outputDateStr;
            } else {
                throw new RuntimeException("Provided date is not valid");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            log.trace("Invalid input date format exception: {}", e.getMessage());
        }
        throw new RuntimeException("Date conversion failed");
    }

    public static Pair<Date, Date> getDateRangeFromCurrentToLastOneWeek() {
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date startDate = calendar.getTime();

        return Pair.of(startDate, endDate);
    }

    public static Pair<Date, Date> getDateRangeFromCurrentToPrevDays(int numberOfDays) {
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_YEAR, -numberOfDays);
        Date startDate = calendar.getTime();

        return Pair.of(startDate, endDate);
    }

    public static String getCurrentDateAsDbFormat() {
        Instant currentInstant = Instant.now();
        ZonedDateTime zonedDateTime = currentInstant.atZone(ZoneId.systemDefault());
        LocalDate datePortion = zonedDateTime.toLocalDate();
        String result = convertStringDateToDbStringFormat(datePortion.toString());
        log.info("current date formatted to db format date as: {} ", result);

        return result;
    }

    public static Pair<Date, Date> getStartAndEndDateOfCurrentMonth() {
        Instant currentInstant = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDate currentDate = currentInstant.atZone(zoneId).toLocalDate();
        LocalDate firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());

        Date startDate = Date.from(firstDayOfMonth.atStartOfDay(zoneId).toInstant());
        Date endDate = Date.from(lastDayOfMonth.atStartOfDay(zoneId).toInstant());

        return Pair.of(startDate, endDate);
    }

    private static boolean isValidDate(Date date, String inputDateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN);
            String formattedDate = dateFormat.format(date);
            return inputDateStr.equals(formattedDate);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public static boolean isDate(String searchKey) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(searchKey, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static Date getCurrentDateTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka")).getTime();
    }

    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Dhaka"));
    }

    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now(ZoneId.of("Asia/Dhaka"));
    }

    public static Long getLocalDateTimeToLong(LocalDateTime localDateTime) {
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Dhaka"));
        return zdt.toInstant().toEpochMilli();
    }

    public static Long getLocalDateToLong(Date date) {
        return date.getTime();
    }
}
