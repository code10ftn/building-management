package com.code10.kts.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Date utilities.
 */
public class DateUtil {

    /**
     * Date seven days in the future.
     */
    public static final Date DATE_FUTURE = createDate(true);

    /**
     * Date seven days in the past.
     */
    public static final Date DATE_PAST = createDate(false);

    /**
     * Creates a date seven days before or after current date.
     *
     * @param future whether to create date in future or past
     * @return future/past date
     */
    private static Date createDate(boolean future) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, future ? 7 : -7);
        return calendar.getTime();
    }
}
