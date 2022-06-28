package j$.util.stream;

import j$.util.DoubleSummaryStatistics;
import j$.util.function.ObjDoubleConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda10 implements ObjDoubleConsumer {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda10 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda10();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda10() {
    }

    @Override // j$.util.function.ObjDoubleConsumer
    public final void accept(Object obj, double d) {
        ((DoubleSummaryStatistics) obj).accept(d);
    }
}
