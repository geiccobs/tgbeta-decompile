package com.google.firebase.appindexing.builders;

import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.gms.common.internal.Preconditions;
import com.google.firebase.appindexing.FirebaseAppIndexingInvalidArgumentException;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.IndexableBuilder;
import com.google.firebase.appindexing.internal.Thing;
import com.google.firebase.appindexing.internal.zzac;
import com.google.firebase.appindexing.internal.zzw;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.Arrays;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public abstract class IndexableBuilder<T extends IndexableBuilder<?>> {
    private final Bundle zza = new Bundle();
    private final String zzb;
    private zzac zzc;
    private String zzd;

    public IndexableBuilder(String type) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotEmpty(type);
        this.zzb = type;
    }

    public static void zza(Bundle bundle, String str, String... strArr) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(strArr);
        String[] strArr2 = (String[]) Arrays.copyOf(strArr, strArr.length);
        if (strArr2.length > 0) {
            int i = 0;
            for (int i2 = 0; i2 < Math.min(strArr2.length, 100); i2++) {
                String str2 = strArr2[i2];
                strArr2[i] = str2;
                if (strArr2[i2] == null) {
                    StringBuilder sb = new StringBuilder(59);
                    sb.append("String at ");
                    sb.append(i2);
                    sb.append(" is null and is ignored by put method.");
                    zzw.zza(sb.toString());
                } else {
                    int length = str2.length();
                    int i3 = Indexable.MAX_STRING_LENGTH;
                    if (length > 20000) {
                        StringBuilder sb2 = new StringBuilder(53);
                        sb2.append("String at ");
                        sb2.append(i2);
                        sb2.append(" is too long, truncating string.");
                        zzw.zza(sb2.toString());
                        String str3 = strArr2[i];
                        if (str3.length() > 20000) {
                            if (Character.isHighSurrogate(str3.charAt(19999)) && Character.isLowSurrogate(str3.charAt(Indexable.MAX_STRING_LENGTH))) {
                                i3 = 19999;
                            }
                            str3 = str3.substring(0, i3);
                        }
                        strArr2[i] = str3;
                    }
                    i++;
                }
            }
            if (i <= 0) {
                return;
            }
            bundle.putStringArray(str, (String[]) zzf((String[]) Arrays.copyOfRange(strArr2, 0, i)));
            return;
        }
        zzw.zza("String array is empty and is ignored by put method.");
    }

    public static void zzb(Bundle bundle, String str, Indexable... indexableArr) throws FirebaseAppIndexingInvalidArgumentException {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(indexableArr);
        Thing[] thingArr = new Thing[indexableArr.length];
        for (int i = 0; i < indexableArr.length; i++) {
            Indexable indexable = indexableArr[i];
            if (indexable == null || (indexable instanceof Thing)) {
                thingArr[i] = (Thing) indexable;
            } else {
                throw new FirebaseAppIndexingInvalidArgumentException("Invalid Indexable encountered. Use Indexable.Builder or convenience methods under Indexables to create the Indexable.");
            }
        }
        zze(bundle, str, thingArr);
    }

    public static void zzc(Bundle bundle, String str, boolean... zArr) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(zArr);
        int length = zArr.length;
        if (length > 0) {
            if (length >= 100) {
                zzw.zza("Input Array of elements is too big, cutting off.");
                zArr = Arrays.copyOf(zArr, 100);
            }
            bundle.putBooleanArray(str, zArr);
            return;
        }
        zzw.zza("Boolean array is empty and is ignored by put method.");
    }

    public static void zzd(Bundle bundle, String str, long... jArr) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(jArr);
        int length = jArr.length;
        if (length > 0) {
            if (length >= 100) {
                zzw.zza("Input Array of elements is too big, cutting off.");
                jArr = Arrays.copyOf(jArr, 100);
            }
            bundle.putLongArray(str, jArr);
            return;
        }
        zzw.zza("Long array is empty and is ignored by put method.");
    }

    private static void zze(Bundle bundle, String str, Thing... thingArr) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(thingArr);
        if (thingArr.length <= 0) {
            zzw.zza("Thing array is empty and is ignored by put method.");
            return;
        }
        int i = 0;
        for (int i2 = 0; i2 < thingArr.length; i2++) {
            thingArr[i] = thingArr[i2];
            if (thingArr[i2] == null) {
                StringBuilder sb = new StringBuilder(58);
                sb.append("Thing at ");
                sb.append(i2);
                sb.append(" is null and is ignored by put method.");
                zzw.zza(sb.toString());
            } else {
                i++;
            }
        }
        if (i <= 0) {
            return;
        }
        bundle.putParcelableArray(str, (Parcelable[]) zzf((Thing[]) Arrays.copyOfRange(thingArr, 0, i)));
    }

    private static <S> S[] zzf(S[] sArr) {
        if (sArr.length < 100) {
            return sArr;
        }
        zzw.zza("Input Array of elements is too big, cutting off.");
        return (S[]) Arrays.copyOf(sArr, 100);
    }

    public final Indexable build() {
        Bundle bundle = new Bundle(this.zza);
        zzac zzacVar = this.zzc;
        if (zzacVar == null) {
            zzacVar = new Indexable.Metadata.Builder().zza();
        }
        return new Thing(bundle, zzacVar, this.zzd, this.zzb);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T put(String key, long... values) {
        zzd(this.zza, key, values);
        return this;
    }

    public T setAlternateName(String... alternateNames) {
        Preconditions.checkNotNull(alternateNames);
        return put("alternateName", alternateNames);
    }

    public final T setDescription(String description) {
        Preconditions.checkNotNull(description);
        return put("description", description);
    }

    public T setId(String id) {
        Preconditions.checkNotNull(id);
        return put("id", id);
    }

    public final T setImage(String url) {
        Preconditions.checkNotNull(url);
        return put(TtmlNode.TAG_IMAGE, url);
    }

    public final <S extends IndexableBuilder> T setIsPartOf(S... sArr) {
        Preconditions.checkNotNull(sArr);
        return put("isPartOf", sArr);
    }

    public final T setKeywords(String... keywords) {
        return put("keywords", keywords);
    }

    public final T setName(String name) {
        Preconditions.checkNotNull(name);
        return put(CommonProperties.NAME, name);
    }

    public final T setSameAs(String webUrl) {
        Preconditions.checkNotNull(webUrl);
        return put("sameAs", webUrl);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final T setUrl(String url) {
        Preconditions.checkNotNull(url);
        this.zzd = url;
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T put(String key, Indexable... values) throws FirebaseAppIndexingInvalidArgumentException {
        zzb(this.zza, key, values);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <S extends IndexableBuilder> T put(String key, S... sArr) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(sArr);
        int length = sArr.length;
        if (length > 0) {
            Thing[] thingArr = new Thing[length];
            for (int i = 0; i < sArr.length; i++) {
                S s = sArr[i];
                if (s == null) {
                    StringBuilder sb = new StringBuilder(60);
                    sb.append("Builder at ");
                    sb.append(i);
                    sb.append(" is null and is ignored by put method.");
                    zzw.zza(sb.toString());
                } else {
                    thingArr[i] = (Thing) s.build();
                }
            }
            zze(this.zza, key, thingArr);
        } else {
            zzw.zza("Builder array is empty and is ignored by put method.");
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T setMetadata(Indexable.Metadata.Builder metadataBuilder) {
        Preconditions.checkState(this.zzc == null, "setMetadata may only be called once");
        Preconditions.checkNotNull(metadataBuilder);
        this.zzc = metadataBuilder.zza();
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T put(String key, String... values) {
        zza(this.zza, key, values);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T put(String key, boolean... values) {
        zzc(this.zza, key, values);
        return this;
    }
}
