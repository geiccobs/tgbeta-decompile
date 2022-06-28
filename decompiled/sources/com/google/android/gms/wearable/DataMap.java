package com.google.android.gms.wearable;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.wearable.zzcc;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.ingestion.models.properties.LongTypedProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class DataMap {
    public static final String TAG = "DataMap";
    private final HashMap<String, Object> zza = new HashMap<>();

    public static ArrayList<DataMap> arrayListFromBundleArrayList(ArrayList<Bundle> arrayList) {
        ArrayList<DataMap> arrayList2 = new ArrayList<>();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList2.add(fromBundle(arrayList.get(i)));
        }
        return arrayList2;
    }

    public static DataMap fromBundle(Bundle bundle) {
        bundle.setClassLoader((ClassLoader) Preconditions.checkNotNull(Asset.class.getClassLoader()));
        DataMap dataMap = new DataMap();
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            if (obj instanceof String) {
                dataMap.putString(str, (String) obj);
            } else if (obj instanceof Integer) {
                dataMap.putInt(str, ((Integer) obj).intValue());
            } else if (obj instanceof Long) {
                dataMap.putLong(str, ((Long) obj).longValue());
            } else if (obj instanceof Double) {
                dataMap.putDouble(str, ((Double) obj).doubleValue());
            } else if (obj instanceof Float) {
                dataMap.putFloat(str, ((Float) obj).floatValue());
            } else if (obj instanceof Boolean) {
                dataMap.putBoolean(str, ((Boolean) obj).booleanValue());
            } else if (obj instanceof Byte) {
                dataMap.putByte(str, ((Byte) obj).byteValue());
            } else if (obj instanceof byte[]) {
                dataMap.putByteArray(str, (byte[]) obj);
            } else if (obj instanceof String[]) {
                dataMap.putStringArray(str, (String[]) obj);
            } else if (obj instanceof long[]) {
                dataMap.putLongArray(str, (long[]) obj);
            } else if (obj instanceof float[]) {
                dataMap.putFloatArray(str, (float[]) obj);
            } else if (obj instanceof Asset) {
                dataMap.putAsset(str, (Asset) obj);
            } else if (obj instanceof Bundle) {
                dataMap.putDataMap(str, fromBundle((Bundle) obj));
            } else if (obj instanceof ArrayList) {
                ArrayList<String> arrayList = (ArrayList) obj;
                switch (zza(arrayList)) {
                    case 0:
                        dataMap.putStringArrayList(str, arrayList);
                        continue;
                    case 1:
                        dataMap.putStringArrayList(str, arrayList);
                        continue;
                    case 2:
                        dataMap.putIntegerArrayList(str, arrayList);
                        continue;
                    case 3:
                        dataMap.putStringArrayList(str, arrayList);
                        continue;
                    case 5:
                        dataMap.putDataMapArrayList(str, arrayListFromBundleArrayList(arrayList));
                        continue;
                }
            }
        }
        return dataMap;
    }

    public static DataMap fromByteArray(byte[] bytes) {
        try {
            return com.google.android.gms.internal.wearable.zzk.zzb(new com.google.android.gms.internal.wearable.zzj(com.google.android.gms.internal.wearable.zzw.zzb(bytes), new ArrayList()));
        } catch (zzcc e) {
            throw new IllegalArgumentException("Unable to convert data", e);
        }
    }

    private static int zza(ArrayList<?> arrayList) {
        if (arrayList.isEmpty()) {
            return 0;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Object obj = arrayList.get(i);
            if (obj != null) {
                if (obj instanceof Integer) {
                    return 2;
                }
                if (obj instanceof String) {
                    return 3;
                }
                if (obj instanceof DataMap) {
                    return 4;
                }
                if (obj instanceof Bundle) {
                    return 5;
                }
            }
        }
        return 1;
    }

    private static final void zzb(String str, Object obj, String str2, Object obj2, ClassCastException classCastException) {
        Log.w(TAG, "Key " + str + " expected " + str2 + " but value was a " + obj.getClass().getName() + ".  The default value " + obj2 + " was returned.");
        Log.w(TAG, "Attempt to cast generated internal exception:", classCastException);
    }

    public void clear() {
        this.zza.clear();
    }

    public boolean containsKey(String key) {
        return this.zza.containsKey(key);
    }

    public boolean equals(Object o) {
        boolean z;
        if (!(o instanceof DataMap)) {
            return false;
        }
        DataMap dataMap = (DataMap) o;
        if (size() != dataMap.size()) {
            return false;
        }
        for (String str : keySet()) {
            Object obj = get(str);
            Object obj2 = dataMap.get(str);
            if (obj instanceof Asset) {
                if (!(obj2 instanceof Asset)) {
                    return false;
                }
                Asset asset = (Asset) obj;
                Asset asset2 = (Asset) obj2;
                if (asset == null || asset2 == null) {
                    if (asset != asset2) {
                        return false;
                    }
                } else {
                    if (!TextUtils.isEmpty(asset.getDigest())) {
                        z = ((String) Preconditions.checkNotNull(asset.getDigest())).equals(asset2.getDigest());
                    } else {
                        z = Arrays.equals(asset.zza(), asset2.zza());
                    }
                    if (!z) {
                        return false;
                    }
                }
            } else if (obj instanceof String[]) {
                if (!(obj2 instanceof String[]) || !Arrays.equals((String[]) obj, (String[]) obj2)) {
                    return false;
                }
            } else if (obj instanceof long[]) {
                if (!(obj2 instanceof long[]) || !Arrays.equals((long[]) obj, (long[]) obj2)) {
                    return false;
                }
            } else if (obj instanceof float[]) {
                if (!(obj2 instanceof float[]) || !Arrays.equals((float[]) obj, (float[]) obj2)) {
                    return false;
                }
            } else if (obj instanceof byte[]) {
                if (!(obj2 instanceof byte[]) || !Arrays.equals((byte[]) obj, (byte[]) obj2)) {
                    return false;
                }
            } else if (obj == null || obj2 == null) {
                return obj == obj2;
            } else if (!obj.equals(obj2)) {
                return false;
            }
        }
        return true;
    }

    public <T> T get(String key) {
        return (T) this.zza.get(key);
    }

    public Asset getAsset(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (Asset) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "Asset", "<null>", e);
            return null;
        }
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    public byte[] getByteArray(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (byte[]) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "byte[]", "<null>", e);
            return null;
        }
    }

    public DataMap getDataMap(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (DataMap) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, TAG, "<null>", e);
            return null;
        }
    }

    public ArrayList<DataMap> getDataMapArrayList(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (ArrayList) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "ArrayList<DataMap>", "<null>", e);
            return null;
        }
    }

    public double getDouble(String key) {
        return getDouble(key, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
    }

    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    public float[] getFloatArray(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (float[]) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "float[]", "<null>", e);
            return null;
        }
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public ArrayList<Integer> getIntegerArrayList(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (ArrayList) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "ArrayList<Integer>", "<null>", e);
            return null;
        }
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long[] getLongArray(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (long[]) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "long[]", "<null>", e);
            return null;
        }
    }

    public String getString(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (String) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "String", "<null>", e);
            return null;
        }
    }

    public String[] getStringArray(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (String[]) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "String[]", "<null>", e);
            return null;
        }
    }

    public ArrayList<String> getStringArrayList(String key) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return null;
        }
        try {
            return (ArrayList) obj;
        } catch (ClassCastException e) {
            zzb(key, obj, "ArrayList<String>", "<null>", e);
            return null;
        }
    }

    public int hashCode() {
        return this.zza.hashCode() * 29;
    }

    public boolean isEmpty() {
        return this.zza.isEmpty();
    }

    public Set<String> keySet() {
        return this.zza.keySet();
    }

    public void putAll(DataMap dataMap) {
        for (String str : dataMap.keySet()) {
            this.zza.put(str, dataMap.get(str));
        }
    }

    public void putAsset(String key, Asset value) {
        this.zza.put(key, value);
    }

    public void putBoolean(String key, boolean value) {
        this.zza.put(key, Boolean.valueOf(value));
    }

    public void putByte(String key, byte value) {
        this.zza.put(key, Byte.valueOf(value));
    }

    public void putByteArray(String key, byte[] value) {
        this.zza.put(key, value);
    }

    public void putDataMap(String key, DataMap value) {
        this.zza.put(key, value);
    }

    public void putDataMapArrayList(String key, ArrayList<DataMap> arrayList) {
        this.zza.put(key, arrayList);
    }

    public void putDouble(String key, double value) {
        this.zza.put(key, Double.valueOf(value));
    }

    public void putFloat(String key, float value) {
        this.zza.put(key, Float.valueOf(value));
    }

    public void putFloatArray(String key, float[] value) {
        this.zza.put(key, value);
    }

    public void putInt(String key, int value) {
        this.zza.put(key, Integer.valueOf(value));
    }

    public void putIntegerArrayList(String key, ArrayList<Integer> arrayList) {
        this.zza.put(key, arrayList);
    }

    public void putLong(String key, long value) {
        this.zza.put(key, Long.valueOf(value));
    }

    public void putLongArray(String key, long[] value) {
        this.zza.put(key, value);
    }

    public void putString(String key, String value) {
        this.zza.put(key, value);
    }

    public void putStringArray(String key, String[] value) {
        this.zza.put(key, value);
    }

    public void putStringArrayList(String key, ArrayList<String> arrayList) {
        this.zza.put(key, arrayList);
    }

    public Object remove(String key) {
        return this.zza.remove(key);
    }

    public int size() {
        return this.zza.size();
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        for (String str : this.zza.keySet()) {
            Object obj = this.zza.get(str);
            if (obj instanceof String) {
                bundle.putString(str, (String) obj);
            } else if (obj instanceof Integer) {
                bundle.putInt(str, ((Integer) obj).intValue());
            } else if (obj instanceof Long) {
                bundle.putLong(str, ((Long) obj).longValue());
            } else if (obj instanceof Double) {
                bundle.putDouble(str, ((Double) obj).doubleValue());
            } else if (obj instanceof Float) {
                bundle.putFloat(str, ((Float) obj).floatValue());
            } else if (obj instanceof Boolean) {
                bundle.putBoolean(str, ((Boolean) obj).booleanValue());
            } else if (obj instanceof Byte) {
                bundle.putByte(str, ((Byte) obj).byteValue());
            } else if (obj instanceof byte[]) {
                bundle.putByteArray(str, (byte[]) obj);
            } else if (obj instanceof String[]) {
                bundle.putStringArray(str, (String[]) obj);
            } else if (obj instanceof long[]) {
                bundle.putLongArray(str, (long[]) obj);
            } else if (obj instanceof float[]) {
                bundle.putFloatArray(str, (float[]) obj);
            } else if (obj instanceof Asset) {
                bundle.putParcelable(str, (Asset) obj);
            } else if (obj instanceof DataMap) {
                bundle.putParcelable(str, ((DataMap) obj).toBundle());
            } else if (obj instanceof ArrayList) {
                ArrayList<String> arrayList = (ArrayList) obj;
                switch (zza(arrayList)) {
                    case 0:
                        bundle.putStringArrayList(str, arrayList);
                        continue;
                    case 1:
                        bundle.putStringArrayList(str, arrayList);
                        continue;
                    case 2:
                        bundle.putIntegerArrayList(str, arrayList);
                        continue;
                    case 3:
                        bundle.putStringArrayList(str, arrayList);
                        continue;
                    case 4:
                        ArrayList<? extends Parcelable> arrayList2 = new ArrayList<>();
                        int size = arrayList.size();
                        for (int i = 0; i < size; i++) {
                            arrayList2.add(((DataMap) arrayList.get(i)).toBundle());
                        }
                        bundle.putParcelableArrayList(str, arrayList2);
                        continue;
                }
            }
        }
        return bundle;
    }

    public byte[] toByteArray() {
        return com.google.android.gms.internal.wearable.zzk.zza(this).zza.zzI();
    }

    public String toString() {
        return this.zza.toString();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return defaultValue;
        }
        try {
            return ((Boolean) obj).booleanValue();
        } catch (ClassCastException e) {
            zzb(key, obj, "Boolean", Boolean.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public byte getByte(String key, byte defaultValue) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return defaultValue;
        }
        try {
            return ((Byte) obj).byteValue();
        } catch (ClassCastException e) {
            zzb(key, obj, "Byte", Byte.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return defaultValue;
        }
        try {
            return ((Double) obj).doubleValue();
        } catch (ClassCastException e) {
            zzb(key, obj, "Double", Double.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public float getFloat(String key, float defaultValue) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return defaultValue;
        }
        try {
            return ((Float) obj).floatValue();
        } catch (ClassCastException e) {
            zzb(key, obj, "Float", Float.valueOf(defaultValue), e);
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return defaultValue;
        }
        try {
            return ((Integer) obj).intValue();
        } catch (ClassCastException e) {
            zzb(key, obj, "Integer", "<null>", e);
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue) {
        Object obj = this.zza.get(key);
        if (obj == null) {
            return defaultValue;
        }
        try {
            return ((Long) obj).longValue();
        } catch (ClassCastException e) {
            zzb(key, obj, LongTypedProperty.TYPE, "<null>", e);
            return defaultValue;
        }
    }

    public String getString(String key, String defaultValue) {
        String key2 = getString(key);
        return key2 == null ? defaultValue : key2;
    }
}
