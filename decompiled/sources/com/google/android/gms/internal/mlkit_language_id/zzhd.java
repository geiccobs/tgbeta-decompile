package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final class zzhd {
    public static String zza(zzdn zzdnVar) {
        zzhc zzhcVar = new zzhc(zzdnVar);
        StringBuilder sb = new StringBuilder(zzhcVar.zza());
        for (int i = 0; i < zzhcVar.zza(); i++) {
            byte zza = zzhcVar.zza(i);
            switch (zza) {
                case 7:
                    sb.append("\\a");
                    break;
                case 8:
                    sb.append("\\b");
                    break;
                case 9:
                    sb.append("\\t");
                    break;
                case 10:
                    sb.append("\\n");
                    break;
                case 11:
                    sb.append("\\v");
                    break;
                case 12:
                    sb.append("\\f");
                    break;
                case 13:
                    sb.append("\\r");
                    break;
                case 34:
                    sb.append("\\\"");
                    break;
                case 39:
                    sb.append("\\'");
                    break;
                case 92:
                    sb.append("\\\\");
                    break;
                default:
                    if (zza >= 32 && zza <= 126) {
                        sb.append((char) zza);
                        break;
                    } else {
                        sb.append('\\');
                        sb.append((char) (((zza >>> 6) & 3) + 48));
                        sb.append((char) (((zza >>> 3) & 7) + 48));
                        sb.append((char) ((zza & 7) + 48));
                        break;
                    }
            }
        }
        return sb.toString();
    }
}
