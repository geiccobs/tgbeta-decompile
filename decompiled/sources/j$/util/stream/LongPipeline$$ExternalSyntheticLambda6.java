package j$.util.stream;

import j$.util.function.LongBinaryOperator;
/* loaded from: classes2.dex */
public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda6 implements LongBinaryOperator {
    public static final /* synthetic */ LongPipeline$$ExternalSyntheticLambda6 INSTANCE = new LongPipeline$$ExternalSyntheticLambda6();

    private /* synthetic */ LongPipeline$$ExternalSyntheticLambda6() {
    }

    @Override // j$.util.function.LongBinaryOperator
    public final long applyAsLong(long j, long j2) {
        return Math.min(j, j2);
    }
}
