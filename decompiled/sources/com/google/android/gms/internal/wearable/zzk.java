package com.google.android.gms.internal.wearable;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzk {
    public static zzj zza(DataMap dataMap) {
        ArrayList arrayList = new ArrayList();
        zzm zzc = zzw.zzc();
        TreeSet treeSet = new TreeSet(dataMap.keySet());
        ArrayList arrayList2 = new ArrayList();
        Iterator it = treeSet.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            Object obj = dataMap.get(str);
            zzn zzc2 = zzv.zzc();
            zzc2.zza(str);
            zzc2.zzb(zzc(arrayList, obj));
            arrayList2.add(zzc2.zzu());
        }
        zzc.zza(arrayList2);
        return new zzj((zzw) zzc.zzu(), arrayList);
    }

    public static DataMap zzb(zzj zzjVar) {
        DataMap dataMap = new DataMap();
        for (zzv zzvVar : zzjVar.zza.zza()) {
            zzd(zzjVar.zzb, dataMap, zzvVar.zza(), zzvVar.zzb());
        }
        return dataMap;
    }

    private static zzu zzc(List<Asset> list, Object obj) {
        zzo zzc = zzu.zzc();
        zzc.zza(zzr.BYTE_ARRAY);
        if (obj == null) {
            zzc.zza(zzr.NULL_VALUE);
            return zzc.zzu();
        }
        zzs zzp = zzt.zzp();
        if (obj instanceof String) {
            zzc.zza(zzr.STRING);
            zzp.zzb((String) obj);
        } else if (obj instanceof Integer) {
            zzc.zza(zzr.INT);
            zzp.zzf(((Integer) obj).intValue());
        } else if (obj instanceof Long) {
            zzc.zza(zzr.LONG);
            zzp.zze(((Long) obj).longValue());
        } else if (obj instanceof Double) {
            zzc.zza(zzr.DOUBLE);
            zzp.zzc(((Double) obj).doubleValue());
        } else if (obj instanceof Float) {
            zzc.zza(zzr.FLOAT);
            zzp.zzd(((Float) obj).floatValue());
        } else if (obj instanceof Boolean) {
            zzc.zza(zzr.BOOLEAN);
            zzp.zzh(((Boolean) obj).booleanValue());
        } else if (obj instanceof Byte) {
            zzc.zza(zzr.BYTE);
            zzp.zzg(((Byte) obj).byteValue());
        } else if (obj instanceof byte[]) {
            zzc.zza(zzr.BYTE_ARRAY);
            zzp.zza(zzau.zzl((byte[]) obj));
        } else if (obj instanceof String[]) {
            zzc.zza(zzr.STRING_ARRAY);
            zzp.zzk(Arrays.asList((String[]) obj));
        } else if (obj instanceof long[]) {
            zzc.zza(zzr.LONG_ARRAY);
            zzp.zzl(zzad.zza((long[]) obj));
        } else if (obj instanceof float[]) {
            zzc.zza(zzr.FLOAT_ARRAY);
            zzp.zzm(zzaa.zza((float[]) obj));
        } else if (obj instanceof Asset) {
            zzc.zza(zzr.ASSET_INDEX);
            list.add((Asset) obj);
            zzp.zzn(list.size() - 1);
        } else {
            int i = 0;
            if (obj instanceof DataMap) {
                zzc.zza(zzr.DATA_BUNDLE);
                DataMap dataMap = (DataMap) obj;
                TreeSet treeSet = new TreeSet(dataMap.keySet());
                zzv[] zzvVarArr = new zzv[treeSet.size()];
                Iterator it = treeSet.iterator();
                while (it.hasNext()) {
                    String str = (String) it.next();
                    zzn zzc2 = zzv.zzc();
                    zzc2.zza(str);
                    zzc2.zzb(zzc(list, dataMap.get(str)));
                    zzvVarArr[i] = zzc2.zzu();
                    i++;
                }
                zzp.zzi(Arrays.asList(zzvVarArr));
            } else if (obj instanceof ArrayList) {
                zzc.zza(zzr.ARRAY_LIST);
                ArrayList arrayList = (ArrayList) obj;
                zzr zzrVar = zzr.NULL_VALUE;
                int size = arrayList.size();
                Object obj2 = null;
                while (i < size) {
                    Object obj3 = arrayList.get(i);
                    zzu zzc3 = zzc(list, obj3);
                    if (zzc3.zza() == zzr.NULL_VALUE || zzc3.zza() == zzr.STRING || zzc3.zza() == zzr.INT || zzc3.zza() == zzr.DATA_BUNDLE) {
                        if (zzrVar != zzr.NULL_VALUE || zzc3.zza() == zzr.NULL_VALUE) {
                            if (zzc3.zza() != zzrVar) {
                                String valueOf = String.valueOf(obj2.getClass());
                                String valueOf2 = String.valueOf(obj3.getClass());
                                StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 80 + String.valueOf(valueOf2).length());
                                sb.append("ArrayList elements must all be of the sameclass, but this one contains a ");
                                sb.append(valueOf);
                                sb.append(" and a ");
                                sb.append(valueOf2);
                                throw new IllegalArgumentException(sb.toString());
                            }
                        } else {
                            zzrVar = zzc3.zza();
                            obj2 = obj3;
                        }
                        zzp.zzj(zzc3);
                        i++;
                    } else {
                        String valueOf3 = String.valueOf(obj3.getClass());
                        StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf3).length() + TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
                        sb2.append("The only ArrayList element types supported by DataBundleUtil are String, Integer, Bundle, and null, but this ArrayList contains a ");
                        sb2.append(valueOf3);
                        throw new IllegalArgumentException(sb2.toString());
                    }
                }
            } else {
                String valueOf4 = String.valueOf(obj.getClass().getSimpleName());
                throw new RuntimeException(valueOf4.length() != 0 ? "newFieldValueFromValue: unexpected value ".concat(valueOf4) : new String("newFieldValueFromValue: unexpected value "));
            }
        }
        zzc.zzb(zzp);
        return zzc.zzu();
    }

    private static void zzd(List<Asset> list, DataMap dataMap, String str, zzu zzuVar) {
        zzr zza = zzuVar.zza();
        if (zza == zzr.NULL_VALUE) {
            dataMap.putString(str, null);
            return;
        }
        zzt zzb = zzuVar.zzb();
        if (zza == zzr.BYTE_ARRAY) {
            dataMap.putByteArray(str, zzb.zza().zzn());
            return;
        }
        int i = 0;
        if (zza == zzr.STRING_ARRAY) {
            dataMap.putStringArray(str, (String[]) zzb.zzl().toArray(new String[0]));
        } else if (zza == zzr.LONG_ARRAY) {
            Object[] array = zzb.zzm().toArray();
            int length = array.length;
            long[] jArr = new long[length];
            while (i < length) {
                Object obj = array[i];
                if (obj == null) {
                    throw null;
                }
                jArr[i] = ((Number) obj).longValue();
                i++;
            }
            dataMap.putLongArray(str, jArr);
        } else if (zza == zzr.FLOAT_ARRAY) {
            Object[] array2 = zzb.zzn().toArray();
            int length2 = array2.length;
            float[] fArr = new float[length2];
            while (i < length2) {
                Object obj2 = array2[i];
                if (obj2 == null) {
                    throw null;
                }
                fArr[i] = ((Number) obj2).floatValue();
                i++;
            }
            dataMap.putFloatArray(str, fArr);
        } else if (zza == zzr.STRING) {
            dataMap.putString(str, zzb.zzb());
        } else if (zza == zzr.DOUBLE) {
            dataMap.putDouble(str, zzb.zzc());
        } else if (zza == zzr.FLOAT) {
            dataMap.putFloat(str, zzb.zzd());
        } else if (zza == zzr.LONG) {
            dataMap.putLong(str, zzb.zze());
        } else if (zza == zzr.INT) {
            dataMap.putInt(str, zzb.zzf());
        } else if (zza == zzr.BYTE) {
            dataMap.putByte(str, (byte) zzb.zzg());
        } else if (zza == zzr.BOOLEAN) {
            dataMap.putBoolean(str, zzb.zzh());
        } else if (zza == zzr.ASSET_INDEX) {
            dataMap.putAsset(str, list.get((int) zzb.zzo()));
        } else if (zza == zzr.DATA_BUNDLE) {
            DataMap dataMap2 = new DataMap();
            for (zzv zzvVar : zzb.zzi()) {
                zzd(list, dataMap2, zzvVar.zza(), zzvVar.zzb());
            }
            dataMap.putDataMap(str, dataMap2);
        } else if (zza == zzr.ARRAY_LIST) {
            List<zzu> zzj = zzb.zzj();
            zzr zzrVar = zzr.NULL_VALUE;
            for (zzu zzuVar2 : zzj) {
                if (zzrVar == zzr.NULL_VALUE) {
                    if (zzuVar2.zza() == zzr.DATA_BUNDLE || zzuVar2.zza() == zzr.STRING || zzuVar2.zza() == zzr.INT) {
                        zzrVar = zzuVar2.zza();
                    } else if (zzuVar2.zza() != zzr.NULL_VALUE) {
                        String valueOf = String.valueOf(zzuVar2.zza());
                        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 37 + String.valueOf(str).length());
                        sb.append("Unexpected TypedValue type: ");
                        sb.append(valueOf);
                        sb.append(" for key ");
                        sb.append(str);
                        throw new IllegalArgumentException(sb.toString());
                    }
                } else if (zzuVar2.zza() != zzrVar) {
                    String valueOf2 = String.valueOf(zzrVar);
                    String valueOf3 = String.valueOf(zzuVar2.zza());
                    int length3 = String.valueOf(str).length();
                    int length4 = String.valueOf(valueOf2).length();
                    StringBuilder sb2 = new StringBuilder(length3 + LocationRequest.PRIORITY_LOW_POWER + length4 + String.valueOf(valueOf3).length());
                    sb2.append("The ArrayList elements should all be the same type, but ArrayList with key ");
                    sb2.append(str);
                    sb2.append(" contains items of type ");
                    sb2.append(valueOf2);
                    sb2.append(" and ");
                    sb2.append(valueOf3);
                    throw new IllegalArgumentException(sb2.toString());
                }
            }
            ArrayList<Integer> arrayList = new ArrayList<>(zzb.zzk());
            for (zzu zzuVar3 : zzb.zzj()) {
                if (zzuVar3.zza() == zzr.NULL_VALUE) {
                    arrayList.add(null);
                } else if (zzrVar == zzr.DATA_BUNDLE) {
                    DataMap dataMap3 = new DataMap();
                    for (zzv zzvVar2 : zzuVar3.zzb().zzi()) {
                        zzd(list, dataMap3, zzvVar2.zza(), zzvVar2.zzb());
                    }
                    arrayList.add(dataMap3);
                } else if (zzrVar == zzr.STRING) {
                    arrayList.add(zzuVar3.zzb().zzb());
                } else if (zzrVar == zzr.INT) {
                    arrayList.add(Integer.valueOf(zzuVar3.zzb().zzf()));
                } else {
                    String valueOf4 = String.valueOf(zzrVar);
                    StringBuilder sb3 = new StringBuilder(String.valueOf(valueOf4).length() + 28);
                    sb3.append("Unexpected typeOfArrayList: ");
                    sb3.append(valueOf4);
                    throw new IllegalArgumentException(sb3.toString());
                }
            }
            if (zzrVar == zzr.NULL_VALUE) {
                dataMap.putStringArrayList(str, arrayList);
            } else if (zzrVar == zzr.DATA_BUNDLE) {
                dataMap.putDataMapArrayList(str, arrayList);
            } else if (zzrVar == zzr.STRING) {
                dataMap.putStringArrayList(str, arrayList);
            } else if (zzrVar == zzr.INT) {
                dataMap.putIntegerArrayList(str, arrayList);
            } else {
                String valueOf5 = String.valueOf(zzrVar);
                StringBuilder sb4 = new StringBuilder(String.valueOf(valueOf5).length() + 28);
                sb4.append("Unexpected typeOfArrayList: ");
                sb4.append(valueOf5);
                throw new IllegalStateException(sb4.toString());
            }
        } else {
            String valueOf6 = String.valueOf(zza);
            StringBuilder sb5 = new StringBuilder(String.valueOf(valueOf6).length() + 32);
            sb5.append("populateBundle: unexpected type ");
            sb5.append(valueOf6);
            throw new RuntimeException(sb5.toString());
        }
    }
}
