package com.microsoft.appcenter.utils;

import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public class TicketCache {
    private static final Map<String, String> sTickets = new HashMap();

    public static String getTicket(String key) {
        return sTickets.get(key);
    }

    public static void putTicket(String key, String value) {
        sTickets.put(key, value);
    }

    public static void clear() {
        sTickets.clear();
    }
}
