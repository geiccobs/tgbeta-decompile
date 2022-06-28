package com.google.android.gms.vision.text;

import java.util.Comparator;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
final class zza implements Comparator<Map.Entry<String, Integer>> {
    public zza(TextBlock textBlock) {
    }

    @Override // java.util.Comparator
    public final /* synthetic */ int compare(Map.Entry<String, Integer> entry, Map.Entry<String, Integer> entry2) {
        return entry.getValue().compareTo(entry2.getValue());
    }
}
