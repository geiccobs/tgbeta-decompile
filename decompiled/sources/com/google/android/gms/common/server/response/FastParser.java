package com.google.android.gms.common.server.response;

import android.util.Log;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.common.util.Base64Utils;
import com.google.android.gms.common.util.JsonUtils;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public class FastParser<T extends FastJsonResponse> {
    private static final char[] zaf = {'u', 'l', 'l'};
    private static final char[] zag = {'r', 'u', 'e'};
    private static final char[] zah = {'r', 'u', 'e', '\"'};
    private static final char[] zai = {'a', 'l', 's', 'e'};
    private static final char[] zaj = {'a', 'l', 's', 'e', '\"'};
    private static final char[] zak = {'\n'};
    private static final zaa<Integer> zam = new zab();
    private static final zaa<Long> zan = new com.google.android.gms.common.server.response.zaa();
    private static final zaa<Float> zao = new zad();
    private static final zaa<Double> zap = new zac();
    private static final zaa<Boolean> zaq = new zaf();
    private static final zaa<String> zar = new zae();
    private static final zaa<BigInteger> zas = new zah();
    private static final zaa<BigDecimal> zat = new zag();
    private final char[] zaa = new char[1];
    private final char[] zab = new char[32];
    private final char[] zac = new char[1024];
    private final StringBuilder zad = new StringBuilder(32);
    private final StringBuilder zae = new StringBuilder(1024);
    private final Stack<Integer> zal = new Stack<>();

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public interface zaa<O> {
        O zaa(FastParser fastParser, BufferedReader bufferedReader) throws ParseException, IOException;
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public static class ParseException extends Exception {
        public ParseException(String str) {
            super(str);
        }

        public ParseException(String str, Throwable th) {
            super(str, th);
        }

        public ParseException(Throwable th) {
            super(th);
        }
    }

    public void parse(InputStream inputStream, T t) throws ParseException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1024);
        try {
            try {
                this.zal.push(0);
                char zaj2 = zaj(bufferedReader);
                switch (zaj2) {
                    case 0:
                        throw new ParseException("No data to parse");
                    case '[':
                        this.zal.push(5);
                        Map<String, FastJsonResponse.Field<?, ?>> fieldMappings = t.getFieldMappings();
                        if (fieldMappings.size() != 1) {
                            throw new ParseException("Object array response class must have a single Field");
                        }
                        FastJsonResponse.Field<?, ?> value = fieldMappings.entrySet().iterator().next().getValue();
                        t.addConcreteTypeArrayInternal(value, value.zae, zaa(bufferedReader, value));
                        break;
                    case '{':
                        this.zal.push(1);
                        zaa(bufferedReader, t);
                        break;
                    default:
                        StringBuilder sb = new StringBuilder(19);
                        sb.append("Unexpected token: ");
                        sb.append(zaj2);
                        throw new ParseException(sb.toString());
                }
                zaa(0);
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.w("FastParser", "Failed to close reader while parsing.");
                }
            } catch (Throwable th) {
                try {
                    bufferedReader.close();
                } catch (IOException e2) {
                    Log.w("FastParser", "Failed to close reader while parsing.");
                }
                throw th;
            }
        } catch (IOException e3) {
            throw new ParseException(e3);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final boolean zaa(BufferedReader bufferedReader, FastJsonResponse fastJsonResponse) throws ParseException, IOException {
        HashMap hashMap;
        Map<String, FastJsonResponse.Field<?, ?>> fieldMappings = fastJsonResponse.getFieldMappings();
        String zaa2 = zaa(bufferedReader);
        if (zaa2 == null) {
            zaa(1);
            return false;
        }
        while (zaa2 != null) {
            FastJsonResponse.Field<?, ?> field = fieldMappings.get(zaa2);
            if (field == null) {
                zaa2 = zab(bufferedReader);
            } else {
                this.zal.push(4);
                switch (field.zaa) {
                    case 0:
                        if (field.zab) {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, (ArrayList<Integer>) zaa(bufferedReader, zam));
                            break;
                        } else {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zad(bufferedReader));
                            break;
                        }
                    case 1:
                        if (field.zab) {
                            fastJsonResponse.zab((FastJsonResponse.Field) field, (ArrayList<BigInteger>) zaa(bufferedReader, zas));
                            break;
                        } else {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zaf(bufferedReader));
                            break;
                        }
                    case 2:
                        if (field.zab) {
                            fastJsonResponse.zac(field, zaa(bufferedReader, zan));
                            break;
                        } else {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zae(bufferedReader));
                            break;
                        }
                    case 3:
                        if (field.zab) {
                            fastJsonResponse.zad(field, zaa(bufferedReader, zao));
                            break;
                        } else {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zag(bufferedReader));
                            break;
                        }
                    case 4:
                        if (field.zab) {
                            fastJsonResponse.zae(field, zaa(bufferedReader, zap));
                            break;
                        } else {
                            fastJsonResponse.zaa(field, zah(bufferedReader));
                            break;
                        }
                    case 5:
                        if (field.zab) {
                            fastJsonResponse.zaf(field, zaa(bufferedReader, zat));
                            break;
                        } else {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zai(bufferedReader));
                            break;
                        }
                    case 6:
                        if (field.zab) {
                            fastJsonResponse.zag(field, zaa(bufferedReader, zaq));
                            break;
                        } else {
                            fastJsonResponse.zaa(field, zaa(bufferedReader, false));
                            break;
                        }
                    case 7:
                        if (field.zab) {
                            fastJsonResponse.zah(field, zaa(bufferedReader, zar));
                            break;
                        } else {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zac(bufferedReader));
                            break;
                        }
                    case 8:
                        fastJsonResponse.zaa((FastJsonResponse.Field) field, Base64Utils.decode(zaa(bufferedReader, this.zac, this.zae, zak)));
                        break;
                    case 9:
                        fastJsonResponse.zaa((FastJsonResponse.Field) field, Base64Utils.decodeUrlSafe(zaa(bufferedReader, this.zac, this.zae, zak)));
                        break;
                    case 10:
                        char zaj2 = zaj(bufferedReader);
                        if (zaj2 == 'n') {
                            zab(bufferedReader, zaf);
                            hashMap = null;
                        } else if (zaj2 != '{') {
                            throw new ParseException("Expected start of a map object");
                        } else {
                            this.zal.push(1);
                            hashMap = new HashMap();
                            while (true) {
                                switch (zaj(bufferedReader)) {
                                    case 0:
                                        throw new ParseException("Unexpected EOF");
                                    case '\"':
                                        String zab = zab(bufferedReader, this.zab, this.zad, null);
                                        if (zaj(bufferedReader) != ':') {
                                            String valueOf = String.valueOf(zab);
                                            throw new ParseException(valueOf.length() != 0 ? "No map value found for key ".concat(valueOf) : new String("No map value found for key "));
                                        } else if (zaj(bufferedReader) != '\"') {
                                            String valueOf2 = String.valueOf(zab);
                                            throw new ParseException(valueOf2.length() != 0 ? "Expected String value for key ".concat(valueOf2) : new String("Expected String value for key "));
                                        } else {
                                            hashMap.put(zab, zab(bufferedReader, this.zab, this.zad, null));
                                            char zaj3 = zaj(bufferedReader);
                                            if (zaj3 != ',') {
                                                if (zaj3 == '}') {
                                                    zaa(1);
                                                    break;
                                                } else {
                                                    StringBuilder sb = new StringBuilder(48);
                                                    sb.append("Unexpected character while parsing string map: ");
                                                    sb.append(zaj3);
                                                    throw new ParseException(sb.toString());
                                                }
                                            }
                                        }
                                    case ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH /* 125 */:
                                        zaa(1);
                                        break;
                                }
                            }
                        }
                        fastJsonResponse.zaa((FastJsonResponse.Field) field, (Map<String, String>) hashMap);
                        break;
                    case 11:
                        if (field.zab) {
                            char zaj4 = zaj(bufferedReader);
                            if (zaj4 == 'n') {
                                zab(bufferedReader, zaf);
                                fastJsonResponse.addConcreteTypeArrayInternal(field, field.zae, null);
                                break;
                            } else {
                                this.zal.push(5);
                                if (zaj4 != '[') {
                                    throw new ParseException("Expected array start");
                                }
                                fastJsonResponse.addConcreteTypeArrayInternal(field, field.zae, zaa(bufferedReader, field));
                                break;
                            }
                        } else {
                            char zaj5 = zaj(bufferedReader);
                            if (zaj5 != 'n') {
                                this.zal.push(1);
                                if (zaj5 != '{') {
                                    throw new ParseException("Expected start of object");
                                }
                                try {
                                    FastJsonResponse zac = field.zac();
                                    zaa(bufferedReader, zac);
                                    fastJsonResponse.addConcreteTypeInternal(field, field.zae, zac);
                                    break;
                                } catch (IllegalAccessException e) {
                                    throw new ParseException("Error instantiating inner object", e);
                                } catch (InstantiationException e2) {
                                    throw new ParseException("Error instantiating inner object", e2);
                                }
                            } else {
                                zab(bufferedReader, zaf);
                                fastJsonResponse.addConcreteTypeInternal(field, field.zae, null);
                                break;
                            }
                        }
                    default:
                        int i = field.zaa;
                        StringBuilder sb2 = new StringBuilder(30);
                        sb2.append("Invalid field type ");
                        sb2.append(i);
                        throw new ParseException(sb2.toString());
                }
                zaa(4);
                zaa(2);
                char zaj6 = zaj(bufferedReader);
                switch (zaj6) {
                    case ',':
                        zaa2 = zaa(bufferedReader);
                        continue;
                    case ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH /* 125 */:
                        zaa2 = null;
                        continue;
                    default:
                        StringBuilder sb3 = new StringBuilder(55);
                        sb3.append("Expected end of object or field separator, but found: ");
                        sb3.append(zaj6);
                        throw new ParseException(sb3.toString());
                }
            }
        }
        zaa(1);
        return true;
    }

    private final String zaa(BufferedReader bufferedReader) throws ParseException, IOException {
        this.zal.push(2);
        char zaj2 = zaj(bufferedReader);
        switch (zaj2) {
            case '\"':
                this.zal.push(3);
                String zab = zab(bufferedReader, this.zab, this.zad, null);
                zaa(3);
                if (zaj(bufferedReader) != ':') {
                    throw new ParseException("Expected key/value separator");
                }
                return zab;
            case ']':
                zaa(2);
                zaa(1);
                zaa(5);
                return null;
            case ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH /* 125 */:
                zaa(2);
                return null;
            default:
                StringBuilder sb = new StringBuilder(19);
                sb.append("Unexpected token: ");
                sb.append(zaj2);
                throw new ParseException(sb.toString());
        }
    }

    private final String zab(BufferedReader bufferedReader) throws ParseException, IOException {
        bufferedReader.mark(1024);
        int i = 1;
        switch (zaj(bufferedReader)) {
            case '\"':
                if (bufferedReader.read(this.zaa) == -1) {
                    throw new ParseException("Unexpected EOF while parsing string");
                }
                char c = this.zaa[0];
                boolean z = false;
                do {
                    if (c == '\"' && !z) {
                        break;
                    } else {
                        if (c == '\\') {
                            z = !z;
                        } else {
                            z = false;
                        }
                        if (bufferedReader.read(this.zaa) == -1) {
                            throw new ParseException("Unexpected EOF while parsing string");
                        }
                        c = this.zaa[0];
                    }
                } while (!Character.isISOControl(c));
                throw new ParseException("Unexpected control character while reading string");
            case ',':
                throw new ParseException("Missing value");
            case '[':
                this.zal.push(5);
                bufferedReader.mark(32);
                if (zaj(bufferedReader) == ']') {
                    zaa(5);
                    break;
                } else {
                    bufferedReader.reset();
                    boolean z2 = false;
                    boolean z3 = false;
                    while (i > 0) {
                        char zaj2 = zaj(bufferedReader);
                        if (zaj2 == 0) {
                            throw new ParseException("Unexpected EOF while parsing array");
                        }
                        if (Character.isISOControl(zaj2)) {
                            throw new ParseException("Unexpected control character while reading array");
                        }
                        if (zaj2 == '\"' && !z2) {
                            z3 = !z3;
                        }
                        if (zaj2 == '[' && !z3) {
                            i++;
                        }
                        if (zaj2 == ']' && !z3) {
                            i--;
                        }
                        if (zaj2 == '\\' && z3) {
                            z2 = !z2;
                        } else {
                            z2 = false;
                        }
                    }
                    zaa(5);
                    break;
                }
                break;
            case '{':
                this.zal.push(1);
                bufferedReader.mark(32);
                char zaj3 = zaj(bufferedReader);
                if (zaj3 == '}') {
                    zaa(1);
                    break;
                } else if (zaj3 == '\"') {
                    bufferedReader.reset();
                    zaa(bufferedReader);
                    do {
                    } while (zab(bufferedReader) != null);
                    zaa(1);
                    break;
                } else {
                    StringBuilder sb = new StringBuilder(18);
                    sb.append("Unexpected token ");
                    sb.append(zaj3);
                    throw new ParseException(sb.toString());
                }
            default:
                bufferedReader.reset();
                zaa(bufferedReader, this.zac);
                break;
        }
        char zaj4 = zaj(bufferedReader);
        switch (zaj4) {
            case ',':
                zaa(2);
                return zaa(bufferedReader);
            case ErrorLogHelper.MAX_PROPERTY_ITEM_LENGTH /* 125 */:
                zaa(2);
                return null;
            default:
                StringBuilder sb2 = new StringBuilder(18);
                sb2.append("Unexpected token ");
                sb2.append(zaj4);
                throw new ParseException(sb2.toString());
        }
    }

    public final String zac(BufferedReader bufferedReader) throws ParseException, IOException {
        return zaa(bufferedReader, this.zab, this.zad, null);
    }

    private final <O> ArrayList<O> zaa(BufferedReader bufferedReader, zaa<O> zaaVar) throws ParseException, IOException {
        char zaj2 = zaj(bufferedReader);
        if (zaj2 == 'n') {
            zab(bufferedReader, zaf);
            return null;
        } else if (zaj2 != '[') {
            throw new ParseException("Expected start of array");
        } else {
            this.zal.push(5);
            ArrayList<O> arrayList = new ArrayList<>();
            while (true) {
                bufferedReader.mark(1024);
                switch (zaj(bufferedReader)) {
                    case 0:
                        throw new ParseException("Unexpected EOF");
                    case ',':
                        break;
                    case ']':
                        zaa(5);
                        return arrayList;
                    default:
                        bufferedReader.reset();
                        arrayList.add(zaaVar.zaa(this, bufferedReader));
                        break;
                }
            }
        }
    }

    private final String zaa(BufferedReader bufferedReader, char[] cArr, StringBuilder sb, char[] cArr2) throws ParseException, IOException {
        switch (zaj(bufferedReader)) {
            case '\"':
                return zab(bufferedReader, cArr, sb, cArr2);
            case 'n':
                zab(bufferedReader, zaf);
                return null;
            default:
                throw new ParseException("Expected string");
        }
    }

    private static String zab(BufferedReader bufferedReader, char[] cArr, StringBuilder sb, char[] cArr2) throws ParseException, IOException {
        boolean z;
        sb.setLength(0);
        bufferedReader.mark(cArr.length);
        boolean z2 = false;
        boolean z3 = false;
        while (true) {
            int read = bufferedReader.read(cArr);
            if (read != -1) {
                for (int i = 0; i < read; i++) {
                    char c = cArr[i];
                    if (Character.isISOControl(c)) {
                        if (cArr2 != null) {
                            for (char c2 : cArr2) {
                                if (c2 == c) {
                                    z = true;
                                    break;
                                }
                            }
                        }
                        z = false;
                        if (!z) {
                            throw new ParseException("Unexpected control character while reading string");
                        }
                    }
                    if (c == '\"' && !z2) {
                        sb.append(cArr, 0, i);
                        bufferedReader.reset();
                        bufferedReader.skip(i + 1);
                        if (z3) {
                            return JsonUtils.unescapeString(sb.toString());
                        }
                        return sb.toString();
                    }
                    if (c == '\\') {
                        z2 = !z2;
                        z3 = true;
                    } else {
                        z2 = false;
                    }
                }
                sb.append(cArr, 0, read);
                bufferedReader.mark(cArr.length);
            } else {
                throw new ParseException("Unexpected EOF while parsing string");
            }
        }
    }

    public final int zad(BufferedReader bufferedReader) throws ParseException, IOException {
        boolean z;
        int i;
        int i2;
        int zaa2 = zaa(bufferedReader, this.zac);
        int i3 = 0;
        if (zaa2 == 0) {
            return 0;
        }
        char[] cArr = this.zac;
        if (zaa2 > 0) {
            if (cArr[0] == '-') {
                i2 = Integer.MIN_VALUE;
                i = 1;
                z = true;
            } else {
                i2 = -2147483647;
                i = 0;
                z = false;
            }
            if (i < zaa2) {
                int i4 = i + 1;
                int digit = Character.digit(cArr[i], 10);
                if (digit < 0) {
                    throw new ParseException("Unexpected non-digit character");
                }
                int i5 = -digit;
                i = i4;
                i3 = i5;
            }
            while (i < zaa2) {
                int i6 = i + 1;
                int digit2 = Character.digit(cArr[i], 10);
                if (digit2 < 0) {
                    throw new ParseException("Unexpected non-digit character");
                }
                if (i3 < -214748364) {
                    throw new ParseException("Number too large");
                }
                int i7 = i3 * 10;
                if (i7 < i2 + digit2) {
                    throw new ParseException("Number too large");
                }
                i3 = i7 - digit2;
                i = i6;
            }
            if (z) {
                if (i > 1) {
                    return i3;
                }
                throw new ParseException("No digits to parse");
            }
            return -i3;
        }
        throw new ParseException("No number to parse");
    }

    public final long zae(BufferedReader bufferedReader) throws ParseException, IOException {
        long j;
        boolean z;
        int zaa2 = zaa(bufferedReader, this.zac);
        long j2 = 0;
        if (zaa2 == 0) {
            return 0L;
        }
        char[] cArr = this.zac;
        if (zaa2 > 0) {
            int i = 0;
            if (cArr[0] == '-') {
                j = Long.MIN_VALUE;
                i = 1;
                z = true;
            } else {
                j = -9223372036854775807L;
                z = false;
            }
            if (i < zaa2) {
                int i2 = i + 1;
                int digit = Character.digit(cArr[i], 10);
                if (digit < 0) {
                    throw new ParseException("Unexpected non-digit character");
                }
                i = i2;
                j2 = -digit;
            }
            while (i < zaa2) {
                int i3 = i + 1;
                int digit2 = Character.digit(cArr[i], 10);
                if (digit2 < 0) {
                    throw new ParseException("Unexpected non-digit character");
                }
                if (j2 < -922337203685477580L) {
                    throw new ParseException("Number too large");
                }
                long j3 = j2 * 10;
                long j4 = digit2;
                if (j3 < j + j4) {
                    throw new ParseException("Number too large");
                }
                j2 = j3 - j4;
                i = i3;
            }
            if (z) {
                if (i > 1) {
                    return j2;
                }
                throw new ParseException("No digits to parse");
            }
            return -j2;
        }
        throw new ParseException("No number to parse");
    }

    public final BigInteger zaf(BufferedReader bufferedReader) throws ParseException, IOException {
        int zaa2 = zaa(bufferedReader, this.zac);
        if (zaa2 == 0) {
            return null;
        }
        return new BigInteger(new String(this.zac, 0, zaa2));
    }

    public final boolean zaa(BufferedReader bufferedReader, boolean z) throws ParseException, IOException {
        while (true) {
            char zaj2 = zaj(bufferedReader);
            switch (zaj2) {
                case '\"':
                    if (z) {
                        throw new ParseException("No boolean value found in string");
                    }
                    z = true;
                case 'f':
                    zab(bufferedReader, z ? zaj : zai);
                    return false;
                case 'n':
                    zab(bufferedReader, zaf);
                    return false;
                case 't':
                    zab(bufferedReader, z ? zah : zag);
                    return true;
                default:
                    StringBuilder sb = new StringBuilder(19);
                    sb.append("Unexpected token: ");
                    sb.append(zaj2);
                    throw new ParseException(sb.toString());
            }
        }
    }

    public final float zag(BufferedReader bufferedReader) throws ParseException, IOException {
        int zaa2 = zaa(bufferedReader, this.zac);
        if (zaa2 == 0) {
            return 0.0f;
        }
        return Float.parseFloat(new String(this.zac, 0, zaa2));
    }

    public final double zah(BufferedReader bufferedReader) throws ParseException, IOException {
        int zaa2 = zaa(bufferedReader, this.zac);
        if (zaa2 == 0) {
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        return Double.parseDouble(new String(this.zac, 0, zaa2));
    }

    public final BigDecimal zai(BufferedReader bufferedReader) throws ParseException, IOException {
        int zaa2 = zaa(bufferedReader, this.zac);
        if (zaa2 == 0) {
            return null;
        }
        return new BigDecimal(new String(this.zac, 0, zaa2));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final <T extends FastJsonResponse> ArrayList<T> zaa(BufferedReader bufferedReader, FastJsonResponse.Field<?, ?> field) throws ParseException, IOException {
        ArrayList<T> arrayList = (ArrayList<T>) new ArrayList();
        char zaj2 = zaj(bufferedReader);
        switch (zaj2) {
            case ']':
                zaa(5);
                return arrayList;
            case 'n':
                zab(bufferedReader, zaf);
                zaa(5);
                return null;
            case '{':
                this.zal.push(1);
                while (true) {
                    try {
                        FastJsonResponse zac = field.zac();
                        if (zaa(bufferedReader, zac)) {
                            arrayList.add(zac);
                            char zaj3 = zaj(bufferedReader);
                            switch (zaj3) {
                                case ',':
                                    if (zaj(bufferedReader) != '{') {
                                        throw new ParseException("Expected start of next object in array");
                                    }
                                    this.zal.push(1);
                                case ']':
                                    zaa(5);
                                    return arrayList;
                                default:
                                    StringBuilder sb = new StringBuilder(19);
                                    sb.append("Unexpected token: ");
                                    sb.append(zaj3);
                                    throw new ParseException(sb.toString());
                            }
                        } else {
                            return arrayList;
                        }
                    } catch (IllegalAccessException e) {
                        throw new ParseException("Error instantiating inner object", e);
                    } catch (InstantiationException e2) {
                        throw new ParseException("Error instantiating inner object", e2);
                    }
                }
            default:
                StringBuilder sb2 = new StringBuilder(19);
                sb2.append("Unexpected token: ");
                sb2.append(zaj2);
                throw new ParseException(sb2.toString());
        }
    }

    private final char zaj(BufferedReader bufferedReader) throws ParseException, IOException {
        if (bufferedReader.read(this.zaa) == -1) {
            return (char) 0;
        }
        while (Character.isWhitespace(this.zaa[0])) {
            if (bufferedReader.read(this.zaa) == -1) {
                return (char) 0;
            }
        }
        return this.zaa[0];
    }

    private final int zaa(BufferedReader bufferedReader, char[] cArr) throws ParseException, IOException {
        int i;
        char zaj2 = zaj(bufferedReader);
        if (zaj2 == 0) {
            throw new ParseException("Unexpected EOF");
        }
        if (zaj2 == ',') {
            throw new ParseException("Missing value");
        }
        if (zaj2 == 'n') {
            zab(bufferedReader, zaf);
            return 0;
        }
        bufferedReader.mark(1024);
        if (zaj2 == '\"') {
            i = 0;
            boolean z = false;
            while (i < cArr.length && bufferedReader.read(cArr, i, 1) != -1) {
                char c = cArr[i];
                if (Character.isISOControl(c)) {
                    throw new ParseException("Unexpected control character while reading string");
                }
                if (c == '\"' && !z) {
                    bufferedReader.reset();
                    bufferedReader.skip(i + 1);
                    return i;
                }
                if (c == '\\') {
                    z = !z;
                } else {
                    z = false;
                }
                i++;
            }
        } else {
            cArr[0] = zaj2;
            i = 1;
            while (i < cArr.length && bufferedReader.read(cArr, i, 1) != -1) {
                if (cArr[i] == '}' || cArr[i] == ',' || Character.isWhitespace(cArr[i]) || cArr[i] == ']') {
                    bufferedReader.reset();
                    bufferedReader.skip(i - 1);
                    cArr[i] = 0;
                    return i;
                }
                i++;
            }
        }
        if (i == cArr.length) {
            throw new ParseException("Absurdly long value");
        }
        throw new ParseException("Unexpected EOF");
    }

    private final void zab(BufferedReader bufferedReader, char[] cArr) throws ParseException, IOException {
        int i = 0;
        while (i < cArr.length) {
            int read = bufferedReader.read(this.zab, 0, cArr.length - i);
            if (read == -1) {
                throw new ParseException("Unexpected EOF");
            }
            for (int i2 = 0; i2 < read; i2++) {
                if (cArr[i2 + i] != this.zab[i2]) {
                    throw new ParseException("Unexpected character");
                }
            }
            i += read;
        }
    }

    private final void zaa(int i) throws ParseException {
        if (this.zal.isEmpty()) {
            StringBuilder sb = new StringBuilder(46);
            sb.append("Expected state ");
            sb.append(i);
            sb.append(" but had empty stack");
            throw new ParseException(sb.toString());
        }
        int intValue = this.zal.pop().intValue();
        if (intValue != i) {
            StringBuilder sb2 = new StringBuilder(46);
            sb2.append("Expected state ");
            sb2.append(i);
            sb2.append(" but had ");
            sb2.append(intValue);
            throw new ParseException(sb2.toString());
        }
    }
}
