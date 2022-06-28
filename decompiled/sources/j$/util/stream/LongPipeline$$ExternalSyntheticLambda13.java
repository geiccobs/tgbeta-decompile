package j$.util.stream;

import j$.util.function.ToLongFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda13 implements ToLongFunction {
    public static final /* synthetic */ LongPipeline$$ExternalSyntheticLambda13 INSTANCE = new LongPipeline$$ExternalSyntheticLambda13();

    private /* synthetic */ LongPipeline$$ExternalSyntheticLambda13() {
    }

    @Override // j$.util.function.ToLongFunction
    public final long applyAsLong(Object obj) {
        long longValue;
        longValue = ((Long) obj).longValue();
        return longValue;
    }
}
