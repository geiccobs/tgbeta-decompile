package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder;
/* loaded from: classes3.dex */
public final /* synthetic */ class BufferSizeAdaptationBuilder$DynamicFormatFilter$$ExternalSyntheticLambda0 implements BufferSizeAdaptationBuilder.DynamicFormatFilter {
    public static final /* synthetic */ BufferSizeAdaptationBuilder$DynamicFormatFilter$$ExternalSyntheticLambda0 INSTANCE = new BufferSizeAdaptationBuilder$DynamicFormatFilter$$ExternalSyntheticLambda0();

    private /* synthetic */ BufferSizeAdaptationBuilder$DynamicFormatFilter$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder.DynamicFormatFilter
    public final boolean isFormatAllowed(Format format, int i, boolean z) {
        return BufferSizeAdaptationBuilder.DynamicFormatFilter.CC.lambda$static$0(format, i, z);
    }
}
