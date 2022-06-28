package com.google.android.gms.common.server.response;

import com.google.android.gms.common.server.response.FastParser;
import java.io.BufferedReader;
import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
final class zaa implements FastParser.zaa<Long> {
    @Override // com.google.android.gms.common.server.response.FastParser.zaa
    public final /* synthetic */ Long zaa(FastParser fastParser, BufferedReader bufferedReader) throws FastParser.ParseException, IOException {
        long zae;
        zae = fastParser.zae(bufferedReader);
        return Long.valueOf(zae);
    }
}
