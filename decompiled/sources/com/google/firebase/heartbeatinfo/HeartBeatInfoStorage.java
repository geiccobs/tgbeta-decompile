package com.google.firebase.heartbeatinfo;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes.dex */
public class HeartBeatInfoStorage {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy z");
    private static HeartBeatInfoStorage instance;
    private final SharedPreferences sharedPreferences;

    private HeartBeatInfoStorage(Context context) {
        this.sharedPreferences = context.getSharedPreferences("FirebaseAppHeartBeat", 0);
        context.getSharedPreferences("FirebaseAppHeartBeatStorage", 0);
    }

    public static synchronized HeartBeatInfoStorage getInstance(Context context) {
        HeartBeatInfoStorage heartBeatInfoStorage;
        synchronized (HeartBeatInfoStorage.class) {
            if (instance == null) {
                instance = new HeartBeatInfoStorage(context);
            }
            heartBeatInfoStorage = instance;
        }
        return heartBeatInfoStorage;
    }

    static boolean isSameDateUtc(long j, long j2) {
        Date date = new Date(j);
        Date date2 = new Date(j2);
        SimpleDateFormat simpleDateFormat = FORMATTER;
        return !simpleDateFormat.format(date).equals(simpleDateFormat.format(date2));
    }

    public synchronized boolean shouldSendSdkHeartBeat(String str, long j) {
        if (this.sharedPreferences.contains(str)) {
            if (!isSameDateUtc(this.sharedPreferences.getLong(str, -1L), j)) {
                return false;
            }
            this.sharedPreferences.edit().putLong(str, j).apply();
            return true;
        }
        this.sharedPreferences.edit().putLong(str, j).apply();
        return true;
    }

    public synchronized boolean shouldSendGlobalHeartBeat(long j) {
        return shouldSendSdkHeartBeat("fire-global", j);
    }
}
