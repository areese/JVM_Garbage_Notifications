// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.
package com.yahoo.garbage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Not sure it's really used here, but this is for TLS of SimpleDateFormat.
 * 
 * @author areese
 *
 */
public class ThreadSafeSimpleDateFormat {
    private final ThreadLocal<DateFormat> tls;

    private static final class SimpleDateFormatThreadLocal extends ThreadLocal<DateFormat> {
        private final String format;
        private final Locale locale;

        public SimpleDateFormatThreadLocal(final String format, final Locale locale) {
            this.format = format;
            this.locale = locale;
        }

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(format, locale);
        }
    }

    private static final class DateFormatThreadLocal extends ThreadLocal<DateFormat> {
        private final int formatStyle;

        public DateFormatThreadLocal(final int formatStyle) {
            this.formatStyle = formatStyle;
        }

        @Override
        protected DateFormat initialValue() {
            return DateFormat.getDateInstance(formatStyle);
        }
    }

    public ThreadSafeSimpleDateFormat(final String format) {
        this(format, Locale.getDefault());
    }

    public ThreadSafeSimpleDateFormat(final int formatStyle) {
        this.tls = new DateFormatThreadLocal(formatStyle);
    }

    public ThreadSafeSimpleDateFormat(final String format, final Locale locale) {
        this.tls = new SimpleDateFormatThreadLocal(format, locale);
    }

    public final Date format(final String date) throws ParseException {
        return tls.get().parse(date);
    }

    public final String format(final Date date) {
        return tls.get().format(date);
    }

    public final long formatLong(final String date) throws ParseException {
        return tls.get().parse(date).getTime();
    }

    public final String format(final long time) {
        return tls.get().format(Long.valueOf(time));
    }

    public final long formatLongNoException(String date) {
        if (null == date) {
            return 0L;
        }

        date = date.trim();

        if (date.isEmpty()) {
            return 0L;
        }

        try {
            return tls.get().parse(date).getTime();
        } catch (ParseException e) {
            return 0L;
        }
    }
}
