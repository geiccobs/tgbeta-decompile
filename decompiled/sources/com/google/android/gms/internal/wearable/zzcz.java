package com.google.android.gms.internal.wearable;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzcz {
    public static String zza(zzcx zzcxVar, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ");
        sb.append(str);
        zzc(zzcxVar, sb, 0);
        return sb.toString();
    }

    public static final void zzb(StringBuilder sb, int i, String str, Object obj) {
        if (obj instanceof List) {
            for (Object obj2 : (List) obj) {
                zzb(sb, i, str, obj2);
            }
        } else if (obj instanceof Map) {
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                zzb(sb, i, str, entry);
            }
        } else {
            sb.append('\n');
            int i2 = 0;
            for (int i3 = 0; i3 < i; i3++) {
                sb.append(' ');
            }
            sb.append(str);
            if (obj instanceof String) {
                sb.append(": \"");
                sb.append(zzdu.zza(zzau.zzm((String) obj)));
                sb.append('\"');
            } else if (obj instanceof zzau) {
                sb.append(": \"");
                sb.append(zzdu.zza((zzau) obj));
                sb.append('\"');
            } else if (obj instanceof zzbs) {
                sb.append(" {");
                zzc((zzbs) obj, sb, i + 2);
                sb.append("\n");
                while (i2 < i) {
                    sb.append(' ');
                    i2++;
                }
                sb.append("}");
            } else if (obj instanceof Map.Entry) {
                sb.append(" {");
                Map.Entry entry2 = (Map.Entry) obj;
                int i4 = i + 2;
                zzb(sb, i4, "key", entry2.getKey());
                zzb(sb, i4, CommonProperties.VALUE, entry2.getValue());
                sb.append("\n");
                while (i2 < i) {
                    sb.append(' ');
                    i2++;
                }
                sb.append("}");
            } else {
                sb.append(": ");
                sb.append(obj.toString());
            }
        }
    }

    private static void zzc(zzcx zzcxVar, StringBuilder sb, int i) {
        Method[] declaredMethods;
        boolean equals;
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        TreeSet<String> treeSet = new TreeSet();
        for (Method method : zzcxVar.getClass().getDeclaredMethods()) {
            hashMap2.put(method.getName(), method);
            if (method.getParameterTypes().length == 0) {
                hashMap.put(method.getName(), method);
                if (method.getName().startsWith("get")) {
                    treeSet.add(method.getName());
                }
            }
        }
        for (String str : treeSet) {
            String substring = str.startsWith("get") ? str.substring(3) : str;
            if (substring.endsWith("List") && !substring.endsWith("OrBuilderList") && !substring.equals("List")) {
                String valueOf = String.valueOf(substring.substring(0, 1).toLowerCase());
                String valueOf2 = String.valueOf(substring.substring(1, substring.length() - 4));
                String concat = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
                Method method2 = (Method) hashMap.get(str);
                if (method2 != null && method2.getReturnType().equals(List.class)) {
                    zzb(sb, i, zzd(concat), zzbs.zzT(method2, zzcxVar, new Object[0]));
                }
            }
            if (substring.endsWith("Map") && !substring.equals("Map")) {
                String valueOf3 = String.valueOf(substring.substring(0, 1).toLowerCase());
                String valueOf4 = String.valueOf(substring.substring(1, substring.length() - 3));
                String concat2 = valueOf4.length() != 0 ? valueOf3.concat(valueOf4) : new String(valueOf3);
                Method method3 = (Method) hashMap.get(str);
                if (method3 != null && method3.getReturnType().equals(Map.class) && !method3.isAnnotationPresent(Deprecated.class) && Modifier.isPublic(method3.getModifiers())) {
                    zzb(sb, i, zzd(concat2), zzbs.zzT(method3, zzcxVar, new Object[0]));
                }
            }
            String valueOf5 = String.valueOf(substring);
            if (((Method) hashMap2.get(valueOf5.length() != 0 ? "set".concat(valueOf5) : new String("set"))) != null) {
                if (substring.endsWith("Bytes")) {
                    String valueOf6 = String.valueOf(substring.substring(0, substring.length() - 5));
                    if (!hashMap.containsKey(valueOf6.length() != 0 ? "get".concat(valueOf6) : new String("get"))) {
                    }
                }
                String valueOf7 = String.valueOf(substring.substring(0, 1).toLowerCase());
                String valueOf8 = String.valueOf(substring.substring(1));
                String concat3 = valueOf8.length() != 0 ? valueOf7.concat(valueOf8) : new String(valueOf7);
                String valueOf9 = String.valueOf(substring);
                Method method4 = (Method) hashMap.get(valueOf9.length() != 0 ? "get".concat(valueOf9) : new String("get"));
                String valueOf10 = String.valueOf(substring);
                Method method5 = (Method) hashMap.get(valueOf10.length() != 0 ? "has".concat(valueOf10) : new String("has"));
                if (method4 != null) {
                    Object zzT = zzbs.zzT(method4, zzcxVar, new Object[0]);
                    if (method5 == null) {
                        if (zzT instanceof Boolean) {
                            if (((Boolean) zzT).booleanValue()) {
                                zzb(sb, i, zzd(concat3), zzT);
                            }
                        } else if (zzT instanceof Integer) {
                            if (((Integer) zzT).intValue() != 0) {
                                zzb(sb, i, zzd(concat3), zzT);
                            }
                        } else if (zzT instanceof Float) {
                            if (((Float) zzT).floatValue() != 0.0f) {
                                zzb(sb, i, zzd(concat3), zzT);
                            }
                        } else if (zzT instanceof Double) {
                            if (((Double) zzT).doubleValue() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                zzb(sb, i, zzd(concat3), zzT);
                            }
                        } else {
                            if (zzT instanceof String) {
                                equals = zzT.equals("");
                            } else if (zzT instanceof zzau) {
                                equals = zzT.equals(zzau.zzb);
                            } else if (zzT instanceof zzcx) {
                                if (zzT != ((zzcx) zzT).zzac()) {
                                    zzb(sb, i, zzd(concat3), zzT);
                                }
                            } else {
                                if ((zzT instanceof Enum) && ((Enum) zzT).ordinal() == 0) {
                                }
                                zzb(sb, i, zzd(concat3), zzT);
                            }
                            if (!equals) {
                                zzb(sb, i, zzd(concat3), zzT);
                            }
                        }
                    } else if (((Boolean) zzbs.zzT(method5, zzcxVar, new Object[0])).booleanValue()) {
                        zzb(sb, i, zzd(concat3), zzT);
                    }
                }
            }
        }
        if (!(zzcxVar instanceof zzbq)) {
            zzdx zzdxVar = ((zzbs) zzcxVar).zzc;
            if (zzdxVar != null) {
                zzdxVar.zzg(sb, i);
                return;
            }
            return;
        }
        zzbl zzblVar = ((zzbq) zzcxVar).zzb;
        throw null;
    }

    private static final String zzd(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (Character.isUpperCase(charAt)) {
                sb.append("_");
            }
            sb.append(Character.toLowerCase(charAt));
        }
        return sb.toString();
    }
}
