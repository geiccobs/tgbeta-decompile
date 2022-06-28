package com.google.android.gms.common.server.response;

import com.google.android.gms.common.server.response.FastParser;
import java.io.BufferedReader;
import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
final class zac implements FastParser.zaa<Double> {
    @Override // com.google.android.gms.common.server.response.FastParser.zaa
    public final /* synthetic */ Double zaa(FastParser fastParser, BufferedReader bufferedReader) throws FastParser.ParseException, IOException {
        double zah;
        zah = fastParser.zah(bufferedReader);
        return Double.valueOf(zah);
    }
}
