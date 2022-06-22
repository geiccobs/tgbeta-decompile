package com.microsoft.appcenter.utils;

import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class TicketCache {
    private static final Map<String, String> sTickets = new HashMap();

    public static String getTicket(String str) {
        return sTickets.get(str);
    }
}
