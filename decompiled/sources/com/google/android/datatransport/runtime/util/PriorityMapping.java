package com.google.android.datatransport.runtime.util;

import android.util.SparseArray;
import com.google.android.datatransport.Priority;
import java.util.HashMap;
/* loaded from: classes3.dex */
public final class PriorityMapping {
    private static HashMap<Priority, Integer> PRIORITY_INT_MAP;
    private static SparseArray<Priority> PRIORITY_MAP = new SparseArray<>();

    static {
        HashMap<Priority, Integer> hashMap = new HashMap<>();
        PRIORITY_INT_MAP = hashMap;
        hashMap.put(Priority.DEFAULT, 0);
        PRIORITY_INT_MAP.put(Priority.VERY_LOW, 1);
        PRIORITY_INT_MAP.put(Priority.HIGHEST, 2);
        for (Priority p : PRIORITY_INT_MAP.keySet()) {
            PRIORITY_MAP.append(PRIORITY_INT_MAP.get(p).intValue(), p);
        }
    }

    public static Priority valueOf(int value) {
        Priority priority = PRIORITY_MAP.get(value);
        if (priority == null) {
            throw new IllegalArgumentException("Unknown Priority for value " + value);
        }
        return priority;
    }

    public static int toInt(Priority priority) {
        Integer integer = PRIORITY_INT_MAP.get(priority);
        if (integer == null) {
            throw new IllegalStateException("PriorityMapping is missing known Priority value " + priority);
        }
        return integer.intValue();
    }
}
