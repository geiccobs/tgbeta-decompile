package com.microsoft.appcenter.ingestion.models.json;

import j$.util.DesugarTimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONException;
/* loaded from: classes3.dex */
public final class JSONDateUtils {
    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() { // from class: com.microsoft.appcenter.ingestion.models.json.JSONDateUtils.1
        @Override // java.lang.ThreadLocal
        public DateFormat initialValue() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            dateFormat.setTimeZone(DesugarTimeZone.getTimeZone("UTC"));
            return dateFormat;
        }
    };

    private static void checkNull(Object date) throws JSONException {
        if (date == null) {
            throw new JSONException("date cannot be null");
        }
    }

    public static String toString(Date date) throws JSONException {
        checkNull(date);
        return DATE_FORMAT.get().format(date);
    }

    public static Date toDate(String date) throws JSONException {
        checkNull(date);
        try {
            return DATE_FORMAT.get().parse(date);
        } catch (ParseException e) {
            throw new JSONException(e.getMessage());
        }
    }
}
